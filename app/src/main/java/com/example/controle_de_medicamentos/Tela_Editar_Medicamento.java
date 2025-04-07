package com.example.controle_de_medicamentos;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Calendar;

public class Tela_Editar_Medicamento extends AppCompatActivity {

    private TextView receberDataHora;
    private EditText editNomeMed;
    private EditText editDesc;
    private EditText editDose;
    private Button btnAdicionarMed;
    private Button btnAtualizarMed;
    private Button btnExcluirMed;
    private SQLiteDatabase bancoDeDados;
    private DatabaseHelper dbHelper;

    private ArrayList<String> itens_admMed;

    Intent get_Intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_editar_medicamento);

        carregarBancoDeDados();

        editNomeMed = findViewById(R.id.editNomeMed);
        editDesc = findViewById(R.id.editDesc);
        editDose = findViewById(R.id.editDose);
        receberDataHora = findViewById(R.id.receberDataHora);

        btnAdicionarMed = findViewById(R.id.btnAdicionarMed);
        btnAtualizarMed = findViewById(R.id.btnAtualizarMed);
        btnExcluirMed = findViewById(R.id.btnExcluirMed);

        get_Intent = getIntent();

        btnAdicionarMed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nomeMed = editNomeMed.getText().toString();
                String descMed = editDesc.getText().toString();
                String doseMed = editDose.getText().toString();
                String dataHora = receberDataHora.getText().toString();

                _AdicionarMedicamento(nomeMed,descMed,doseMed,dataHora);

                System.out.println("Ola");
                Intent resultadoIntent = new Intent();
                setResult(RESULT_OK, resultadoIntent); // Enviando os dados de volta
                finish();

            }
        });


        btnAtualizarMed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String dataHoraAtt = receberDataHora.getText().toString();
                Integer id = get_Intent.getIntExtra("id", 0);
                _AtualizarMedicamento(dataHoraAtt, id);
                Intent resultadoIntent = new Intent();
                setResult(RESULT_OK, resultadoIntent); // Enviando os dados de volta
                finish();
            }
        });

        btnExcluirMed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _ExcluirMedicamento();
            }
        });

    }


    /*public void carregarTarefa() {
        try {

            nomeMed = intent.getStringExtra("tarefa");
            dataHora = intent.getStringExtra("dataHora");
            id = intent.getIntExtra("id",0);


            editText.setText(tarefa);
            receberDataHora.setText(dataHora);

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
    */
    private void carregarBancoDeDados() {
        dbHelper = new DatabaseHelper(this);
        bancoDeDados = dbHelper.getWritableDatabase(); // Abre o banco para leitura e escrita
    }
    public void _pegarDataHoraAtt(View view) {
        Calendar calendario = Calendar.getInstance();
        int dia = calendario.get(Calendar.DAY_OF_MONTH);
        int mes = calendario.get(Calendar.MONTH);
        int ano = calendario.get(Calendar.YEAR);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view1, year, month, dayOfMonth) -> {
                    //Atualiza o textview com a data selecionada
                    String dataSelecionada = String.format("%02d/%02d/%04d", dayOfMonth, month + 1, year);
                    receberDataHora.setText(dataSelecionada);
                    //Após escolher a data, chama o método para escolher a hora
                    _pegarHora(null);
                }, ano, mes, dia);
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
        datePickerDialog.show();
    }

    private void _pegarHora(View view) {
        Calendar calendario = Calendar.getInstance();
        int hora = calendario.get(Calendar.HOUR_OF_DAY);
        int minuto = calendario.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                (view12, hourOfDay, minute) -> {

                    //Formatar Hora
                    String time = String.format("%02d:%02d", hourOfDay, minute);
                    //Concatena a data que ja existe com a hora
                    receberDataHora.setText(receberDataHora.getText().toString() + " " + time);

                }, hora, minuto, true);
        timePickerDialog.show();
    }


    private void _AdicionarMedicamento(String nomeMed,String descMed,String doseMed,String dataHora) {
        SQLiteStatement stmt = null;

        try {
//            String admMed = itens_admMed.toString();

            String sql = "INSERT INTO medicamentos (nomeMedicamento, dataHora, status, admMedicamento, descricao, dose) VALUES (?, ?, ?, ?, ?, ?)";
            stmt = bancoDeDados.compileStatement(sql);
            stmt.bindString(1, nomeMed); // nome
            stmt.bindString(2, dataHora); // data e hora
            stmt.bindString(3, "1"); //status de ativo
            stmt.bindString(4, "adm medica"); // administração do medicamento
            stmt.bindString(5, descMed); // descrição do medicamento
            stmt.bindString(6, doseMed); // dose do medicamento

            long id = stmt.executeInsert();


            Toast.makeText(Tela_Editar_Medicamento.this, "Medicamento adicionado com sucesso! Com id="+id, Toast.LENGTH_SHORT).show();


        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            stmt.close();
        }

    }

    private void _AtualizarMedicamento(String DataHoraAtt, Integer id) {
        SQLiteStatement stmt = null;
        try {
            //Usando o SQLiteStatement para segurança e eficiência
            String sql = "UPDATE medicamentos SET nomeMed = ?, dataHora = ?, status = ?, admMed = ?, descMed = ?, doseMed =? WHERE id = ?";
            stmt = bancoDeDados.compileStatement(sql);
            stmt.bindString(1, editNomeMed.getText().toString());
            stmt.bindString(2, DataHoraAtt);
            stmt.bindString(3, "1");
            stmt.bindString(4, itens_admMed.toString());
            stmt.bindString(5, editDesc.getText().toString());
            stmt.bindString(6, editDose.getText().toString());
            stmt.bindLong(7, id);

            stmt.executeUpdateDelete();
        } catch (Exception e) {
            e.printStackTrace();
            // Aqui você pode adicionar um Toast de erro se desejar
            Toast.makeText(this, "Erro ao atualizar a tarefa!", Toast.LENGTH_SHORT).show();
        } finally {
            if (stmt != null) {
                stmt.close();
            }
        }
    }

    private void _ExcluirMedicamento() {
        try {
        } catch (Exception e) {
        }
    }
}