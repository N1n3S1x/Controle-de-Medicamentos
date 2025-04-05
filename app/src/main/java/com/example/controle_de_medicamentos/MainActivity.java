package com.example.controle_de_medicamentos;

import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;

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
    private ArrayAdapter<String> adaptador;
    private TextView campoDataHora;
    private SQLiteDatabase bancoDeDados;






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inicializar componentes da interface
        editTextFiltro = findViewById(R.id.editTextFiltro);
        botaoInserir = findViewById(R.id.botaoInserir);
        minhaListView = findViewById(R.id.minhaListView);



        _Criar_Banco_De_Dados();

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

        carregarItens();

    }

    public void _Criar_Banco_De_Dados() {
        try {
            bancoDeDados = openOrCreateDatabase("ListaTarefasApp", MODE_PRIVATE, null);
            bancoDeDados.execSQL("CREATE TABLE IF NOT EXISTS " +
                    "medicamentos (id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "nomeMedicamento VARCHAR, dataHora VARCHAR, status VARCHAR,admMedicamento VARCHAR, descricao VARCHAR)");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void carregarItens() {
        try {
            Cursor cursor = bancoDeDados.rawQuery("SELECT * FROM tarefas " +
                    "ORDER BY id DESC", null);
            //indíces das colunas
            int indiceId = cursor.getColumnIndex("id");
            int indiceMedicamento = cursor.getColumnIndex("nomeMedicamento");
            int indiceDataHora = cursor.getColumnIndex("dataHora");
            int indiceStatus = cursor.getColumnIndex("status");
            int indice_admMed = cursor.getColumnIndex("admMedicamento");
            int indice_descMed = cursor.getColumnIndex("descricao");

            //inicializar as listas
            ids = new ArrayList<>();
            itens = new ArrayList<>();
            datasHoras = new ArrayList<>();
            statusMedicamento = new ArrayList<>();
            descricaoMedicamento = new ArrayList<>();

            //adaptador usando o layout customizado
            adaptador = new ArrayAdapter<String>(this, R.layout.linhacustomizada,
                    R.id.texto1, itens) {
                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    View view = super.getView(position, convertView, parent);
                    TextView texto1 = view.findViewById(R.id.texto1);
                    TextView texto2 = view.findViewById(R.id.texto2);
                    TextView texto3 = view.findViewById(R.id.texto3);

                    // Define o valor do TextView de data/hora com base na posição -- retornando do BD
                    texto1.setText(itens.get(position));
                    texto2.setText(datasHoras.get(position));
                    texto3.setText(admMedicamento.get(position));

                    // Define o estilo do texto com base no status da tarefa
                    if (statusMedicamento.get(position).equals("1")) {
                        texto1.setTextColor(Color.BLACK);
                        texto2.setTextColor(Color.BLUE);
                        texto3.setTextColor(Color.BLACK);
                        texto1.setPaintFlags(texto1.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                        texto2.setPaintFlags(texto2.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                        texto3.setPaintFlags(texto3.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                    } else {
                        texto1.setTextColor(Color.GRAY);
                        texto2.setTextColor(Color.GRAY);
                        texto3.setTextColor(Color.GRAY);
                        texto1.setPaintFlags(texto1.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                        texto2.setPaintFlags(texto2.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                        texto3.setPaintFlags(texto3.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
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
                cursor.moveToNext();
            }
            cursor.close(); //liberando memória, tornando mais eficiente

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void alternarStatusTarefa(int position) {
        try {
            String novoStatus = statusMedicamento.get(position).equals("1") ? "0" : "1";
            bancoDeDados.execSQL("UPDATE tarefas SET status = '" +
                    novoStatus + "' WHERE id = " + ids.get(position));
            carregarItens();
            if (novoStatus.equals("1")) {
                Toast.makeText(MainActivity.this, "Medicamento em Uso!",
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(MainActivity.this, "Medicamento Finalizado!",
                        Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void editarMedicamento(int position) {
        new AlertDialog.Builder(MainActivity.this)
                .setTitle("Editar Tarefa")
                .setMessage("Deseja editar a tarefa \"" + itens.get(position) + "\"?")
                .setPositiveButton("Sim", (dialog, which) -> {
                    mudarTelaEdicao(position);
                })
                .setNegativeButton("Não", null)
                .setNeutralButton("Compartilhar", (dialogInterface, i) -> {
                    //compartilharTarefa(position);
                })

                .show();
    }
    private void mudarTelaEdicao(int position) {
        try {

            Toast.makeText(MainActivity.this, "Editar Medicamento!",
                    Toast.LENGTH_SHORT).show();


            Intent intent = new Intent(MainActivity.this, Tela_Editar_Medicamento.class);
            intent.putExtra("id", ids.get(position));
            intent.putExtra("NomeMedicamento", itens.get(position));
            intent.putExtra("dataHora", datasHoras.get(position));
            intent.putExtra("status", statusMedicamento.get(position));
            intent.putExtra("admMedicamento", admMedicamento.get(position));

            startActivity(intent);
//            finish();


        }catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void mudarTelaInsercao(){
        try {
            Intent intent = new Intent(MainActivity.this, Tela_Editar_Medicamento.class);
            startActivity(intent);
            finish();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}