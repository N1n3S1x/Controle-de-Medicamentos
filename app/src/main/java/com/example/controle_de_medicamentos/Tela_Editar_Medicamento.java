package com.example.controle_de_medicamentos;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

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

    private ArrayList<String> itens_admMed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_tela_editar_medicamento);

        editNomeMed = findViewById(R.id.editNomeMed);
        editDesc = findViewById(R.id.editDesc);
        editDose = findViewById(R.id.editDose);
        receberDataHora = findViewById(R.id.receberDataHora);

        btnAdicionarMed = findViewById(R.id.btnAdicionarMed);
        btnAtualizarMed = findViewById(R.id.btnAtualizarMed);
        btnExcluirMed = findViewById(R.id.btnExcluirMed);

        btnAdicionarMed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _AdicionarMedicamento();
            }
        });


        btnAtualizarMed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _AtualizarMedicamento();
            }
        });

        btnExcluirMed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _ExcluirMedicamento();
            }
        });

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

    private void _AdicionarMedicamento(){
        try {


            String nomeMed = editNomeMed.getText().toString();
            String descMed = editDesc.getText().toString();
            String doseMed = editDose.getText().toString();
            String dataHora = receberDataHora.getText().toString();

            String admMed = itens_admMed.toString();


            String sql = "INSERT INTO medicamentos (nomeMedicamento, dataHora, status, admMedicamento, descricao, dose) VALUES (?, ?, ?, ?, ?, ?)";
            SQLiteStatement stmt = bancoDeDados.compileStatement(sql);
            stmt.bindString(1, nomeMed);
            stmt.bindString(2, dataHora);
            stmt.bindString(3, "1"); //status de ativo
            stmt.bindString(4, admMed); //status de ativo
            stmt.bindString(5, descMed); //status de ativo
            stmt.bindString(5, doseMed); //status de ativo
            stmt.executeInsert();


            Toast.makeText(Tela_Editar_Medicamento.this, "Medicamento adicionado com sucesso!",
                    Toast.LENGTH_SHORT).show();



        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void _AtualizarMedicamento() {
        try {
        } catch (Exception e) {
        }
    }
    private void _ExcluirMedicamento() {
        try {
        } catch (Exception e) {
        }
    }
}