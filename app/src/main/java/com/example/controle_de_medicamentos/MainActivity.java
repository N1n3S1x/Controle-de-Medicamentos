package com.example.controle_de_medicamentos;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


public class MainActivity extends AppCompatActivity {

    private EditText editTextFiltro;
    private Button botaoInserir;
    private ListView minhaListView;
    private ArrayList<String> itens;
    private ArrayList<String> datasHoras;
    private ArrayList<Integer> ids;
    private ArrayList<String> statusMedicamento;
    private ArrayList<String> descricaoMedicamento;
    private ArrayList<String> admMedicamento;
    private ArrayList<String> doseMedicamento;
    private ArrayList<Integer> intervalo_horas;
    private ArrayList<Integer> duracao_dias;
    private ArrayAdapter<String> adaptador;
    private DatabaseHelper dbHelper;
    private TextView campoDataHora;
    private SQLiteDatabase bancoDeDados;
    private TextView textoStatus;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);


        // Inicializar componentes da interface
//        editTextFiltro = findViewById(R.id.editTextFiltro);
        botaoInserir = findViewById(R.id.botaoInserir);
        minhaListView = findViewById(R.id.minhaListView);


        _Criar_Banco_De_Dados();
        // permissões para notificações 8.0+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("canal_notificacao", "Canal de Notificações", NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("Notificações do Controle de Medicamentos");
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }

        botaoInserir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mudarTelaInsercao();
            }
        });

        minhaListView.setOnItemClickListener(((parent, view, position, id) -> {
            alternarStatusTarefa(position);
        }));

        minhaListView.setOnItemLongClickListener(((parent, view, position, id) -> {

            editarMedicamento(position);

            return true;
        }));

        _CarregarItens();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();

        searchView.setQueryHint("Buscar medicamento ou tipo...");

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                _FiltrarItens(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                _FiltrarItens(newText);
                return false;
            }
        });

        return true;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK) { // ✅ Verifica se veio da MainActivity2
            int id = data.getIntExtra("id", 0);
            String tarefaAtualizada = data.getStringExtra("tarefa");
            String dataHoraAtualizada = data.getStringExtra("dataHora");

            // Atualiza os dados no banco ou na interface
            _CarregarItens();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        _CarregarItens(); // Recarrega a lista sempre que voltar para MainActivity
    }

    public void _Criar_Banco_De_Dados() {
        try {

            DatabaseHelper databaseHelper = new DatabaseHelper(this);
            bancoDeDados = databaseHelper.getWritableDatabase();
            databaseHelper.onCreate(bancoDeDados);

//            bancoDeDados = openOrCreateDatabase("ControleMedicamentos", MODE_PRIVATE, null);
//            bancoDeDados.execSQL("CREATE TABLE IF NOT EXISTS " +
//                    "medicamentos (id INTEGER PRIMARY KEY AUTOINCREMENT," +
//                    "nomeMedicamento VARCHAR, dataHora VARCHAR, status VARCHAR,admMedicamento VARCHAR," +
//                    " descricao VARCHAR, dose VARCHAR, intervalo_horas INTEGER, duracao_dias INTEGER)");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void _CarregarItens() {
        try {
            Cursor cursor = bancoDeDados.rawQuery("SELECT * FROM medicamentos " + "ORDER BY id DESC", null);
            //indíces das colunas
            int indiceId = cursor.getColumnIndex("id");
            int indiceMedicamento = cursor.getColumnIndex("nomeMedicamento");
            int indiceDataHora = cursor.getColumnIndex("dataHora");
            int indiceStatus = cursor.getColumnIndex("status");
            int indice_admMed = cursor.getColumnIndex("admMedicamento");
            int indice_descMed = cursor.getColumnIndex("descricao");
            int indice_doseMed = cursor.getColumnIndex("dose");
            int indice_duracao = cursor.getColumnIndex("duracao_dias");
            int indice_intervalo = cursor.getColumnIndex("intervalo_horas");

            //inicializar as listas
            ids = new ArrayList<>();
            itens = new ArrayList<>();
            datasHoras = new ArrayList<>();
            statusMedicamento = new ArrayList<>();
            descricaoMedicamento = new ArrayList<>();
            admMedicamento = new ArrayList<>();
            doseMedicamento = new ArrayList<>();
            intervalo_horas = new ArrayList<>();
            duracao_dias = new ArrayList<>();

            //inicializar o adaptador
            //adaptador usando o layout customizado
            adaptador = new ArrayAdapter<String>(this, R.layout.linhacustomizada, R.id.texto1, itens) {
                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    View view = super.getView(position, convertView, parent);
                    TextView texto1 = view.findViewById(R.id.texto1);
                    TextView texto2 = view.findViewById(R.id.texto2);
                    TextView texto3 = view.findViewById(R.id.texto3);
                    TextView texto4 = view.findViewById(R.id.texto4);
                    TextView texto5 = view.findViewById(R.id.texto5);
                    TextView textoStatus = view.findViewById(R.id.statusLabel);

                    // Define o valor do TextView de data/hora com base na posição -- retornando do BD
                    String statusLabel;
                    texto1.setText("Nome Medicamento: " + itens.get(position) );

                    texto2.setText("Horário: " + datasHoras.get(position));
                    texto3.setText("Adm. Medicamento: " + admMedicamento.get(position));
                    texto4.setText("Dose: " + doseMedicamento.get(position));
                    texto5.setText("Descrição: " + descricaoMedicamento.get(position));


                    statusLabel = statusMedicamento.get(position).equals("1") ? "Pendente" : "Tomado";
                    textoStatus.setText("Status: " + statusLabel);

                    // Define o estilo do texto com base no status da tarefa
                    if (statusMedicamento.get(position).equals("1")) {
                        texto1.setTextColor(Color.BLACK);
                        texto2.setTextColor(Color.BLUE);
                        texto3.setTextColor(Color.BLACK);
                        texto4.setTextColor(Color.BLACK);
                        texto5.setTextColor(Color.BLACK);

                        texto1.setPaintFlags(texto1.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                        texto2.setPaintFlags(texto2.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                        texto3.setPaintFlags(texto3.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                        texto4.setPaintFlags(texto4.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                        texto5.setPaintFlags(texto5.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                    } else {
                        texto1.setTextColor(Color.GRAY);
                        texto2.setTextColor(Color.GRAY);
                        texto3.setTextColor(Color.GRAY);
                        texto4.setTextColor(Color.GRAY);
                        texto5.setTextColor(Color.GRAY);

                        texto1.setPaintFlags(texto1.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                        texto2.setPaintFlags(texto2.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                        texto3.setPaintFlags(texto3.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                        texto4.setPaintFlags(texto4.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                        texto5.setPaintFlags(texto5.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    }

                    return view;
                }
            };
            minhaListView.setAdapter(adaptador);

            //itera sober o cursor e adiciona às listas
            cursor.moveToFirst();
            while (cursor != null) {
                ids.add(cursor.getInt(indiceId));
                itens.add(cursor.getString(indiceMedicamento));
                datasHoras.add(cursor.getString(indiceDataHora));
                statusMedicamento.add(cursor.getString(indiceStatus));
                admMedicamento.add(cursor.getString(indice_admMed));
                descricaoMedicamento.add(cursor.getString(indice_descMed));
                doseMedicamento.add(cursor.getString(indice_doseMed));
                intervalo_horas.add(cursor.getInt(indice_intervalo));
                duracao_dias.add(cursor.getInt(indice_duracao));

                // Agendar notificação
                if (statusMedicamento.get(ids.size() - 1).equals("1")) {
                    agendarNotificacao(datasHoras.get(ids.size() - 1), itens.get(ids.size() - 1), ids.get(ids.size() - 1));
                }
                cursor.moveToNext();
            }
            cursor.close(); //liberando memória, tornando mais eficiente

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void _FiltrarItens(String filtro) {
        try {
            Cursor cursor = bancoDeDados.rawQuery("SELECT * FROM medicamentos WHERE nomeMedicamento LIKE ? OR admMedicamento LIKE ? ORDER BY id DESC", new String[]{"%" + filtro + "%", "%" + filtro + "%"});

            int indiceId = cursor.getColumnIndex("id");
            int indiceMedicamento = cursor.getColumnIndex("nomeMedicamento");
            int indiceDataHora = cursor.getColumnIndex("dataHora");
            int indiceStatus = cursor.getColumnIndex("status");
            int indiceAdmMed = cursor.getColumnIndex("admMedicamento");
            int indiceDescMed = cursor.getColumnIndex("descricao");
            int indiceDoseMed = cursor.getColumnIndex("dose");
            int indiceIntervalo = cursor.getColumnIndex("intervalo_horas");
            int indiceDuracao = cursor.getColumnIndex("duracao_dias");


            ids.clear();
            itens.clear();
            datasHoras.clear();
            statusMedicamento.clear();
            admMedicamento.clear();
            descricaoMedicamento.clear();
            doseMedicamento.clear();

            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                ids.add(cursor.getInt(indiceId));
                itens.add(cursor.getString(indiceMedicamento));
                datasHoras.add(cursor.getString(indiceDataHora));
                statusMedicamento.add(cursor.getString(indiceStatus));
                admMedicamento.add(cursor.getString(indiceAdmMed));
                descricaoMedicamento.add(cursor.getString(indiceDescMed));
                doseMedicamento.add(cursor.getString(indiceDoseMed));
                intervalo_horas.add(cursor.getInt(indiceIntervalo));
                duracao_dias.add(cursor.getInt(indiceDuracao));


                cursor.moveToNext();
            }

            cursor.close();
            adaptador.notifyDataSetChanged();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void agendarNotificacao(String horario, String nomeMedicamento, int idMedicamento) {
        try {
            // Parse do horário (formato HH:mm)
            SimpleDateFormat formatoHora = new SimpleDateFormat("HH:mm");
            Date hora = formatoHora.parse(horario);

            Calendar calendarioAgora = Calendar.getInstance();
            Calendar calendarioAlarme = Calendar.getInstance();
            calendarioAlarme.setTime(hora);

            // Ajustar para o mesmo dia de hoje
            calendarioAlarme.set(Calendar.YEAR, calendarioAgora.get(Calendar.YEAR));
            calendarioAlarme.set(Calendar.MONTH, calendarioAgora.get(Calendar.MONTH));
            calendarioAlarme.set(Calendar.DAY_OF_MONTH, calendarioAgora.get(Calendar.DAY_OF_MONTH));

            // Se o horário já passou hoje, agendar para amanhã
            if (calendarioAlarme.before(calendarioAgora)) {
                calendarioAlarme.add(Calendar.DAY_OF_MONTH, 1);
            }

            Intent intent = new Intent(this, AlarmReceiver.class);
            intent.putExtra("nome", nomeMedicamento);

            PendingIntent pendingIntent = PendingIntent.getBroadcast(this, idMedicamento, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

            AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
            long intervalo = AlarmManager.INTERVAL_DAY;

            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendarioAlarme.getTimeInMillis(), intervalo, pendingIntent);

            Log.d("Alarme", "Notificação agendada para: " + calendarioAlarme.getTime());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void cancelarNotificacao(int idMedicamento) {
        Intent intent = new Intent(this, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, idMedicamento, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
    }

    private void alternarStatusTarefa(int position) {
        try {
            String novoStatus = statusMedicamento.get(position).equals("1") ? "0" : "1";
            int idMedicamento = ids.get(position);

            bancoDeDados.execSQL("UPDATE medicamentos SET status = '" + novoStatus + "' WHERE id = " + idMedicamento);

            if (novoStatus.equals("1")) {
                agendarNotificacao(datasHoras.get(position), itens.get(position), idMedicamento);
                Toast.makeText(MainActivity.this, "Medicamento pendente!", Toast.LENGTH_SHORT).show();
            } else {
                cancelarNotificacao(idMedicamento);
                Toast.makeText(MainActivity.this, "Medicamento Tomado!", Toast.LENGTH_SHORT).show();
            }

            _CarregarItens();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void editarMedicamento(int position) {
        new AlertDialog.Builder(MainActivity.this).setTitle("Editar medicamento").setMessage("Deseja editar " +
                        "o medicamento \"" + itens.get(position) + "\"?").setPositiveButton("Sim", (dialog, which) -> {
                    mudarTelaEdicao(position);})
                .setNegativeButton("Não", null)


                .show();
    }

    private void mudarTelaEdicao(int position) {
        try {

            Toast.makeText(MainActivity.this, "Editar Medicamento!", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(MainActivity.this, Tela_Editar_Medicamento.class);
            intent.putExtra("id", ids.get(position));
            intent.putExtra("NomeMedicamento", itens.get(position));
            intent.putExtra("dataHora", datasHoras.get(position));
            intent.putExtra("status", statusMedicamento.get(position));
            intent.putExtra("admMedicamento", admMedicamento.get(position));


            intent.putExtra("edicao_Med", true);

            intent.putExtra("descricaoMedicamento", (descricaoMedicamento.size() > position ? descricaoMedicamento.get(position) : ""));
            intent.putExtra("doseMedicamento", (doseMedicamento.size() > position ? doseMedicamento.get(position) : ""));

            intent.putExtra("intervalo_horas", (intervalo_horas.size() > position ? intervalo_horas.get(position).toString() : ""));
            intent.putExtra("duracao_dias", (duracao_dias.size() > position ? duracao_dias.get(position).toString() : ""));

//            startActivity(intent);
            startActivityForResult(intent, 1);


        } catch (Exception e) {

            e.printStackTrace();
        }

    }

    private void mudarTelaInsercao() {
        try {
            Intent intent = new Intent(MainActivity.this, Tela_Editar_Medicamento.class);
//            startActivity(intent);
            startActivityForResult(intent, 1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}