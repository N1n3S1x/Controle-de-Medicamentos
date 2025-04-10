package com.example.controle_de_medicamentos;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;
import java.util.Locale;

public class Tela_Editar_Medicamento extends AppCompatActivity {

    Intent get_Intent;
    private TextView txtHorarioSelecionado;
    private EditText editNomeMed;
    private EditText editDesc;
    private EditText editDose;
    private EditText editIntervalo;
    private EditText editDuracao;
    private Button btnAdicionarMed;
    private Button btnAtualizarMed;
    private Button btnExcluirMed;
    private SQLiteDatabase bancoDeDados;
    private DatabaseHelper dbHelper;
    private Spinner spinnerAdm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_editar_medicamento);

        carregarBancoDeDados();

        editNomeMed = findViewById(R.id.editNomeMed);
        editDesc = findViewById(R.id.editDesc);
        editDose = findViewById(R.id.editDose);
        editIntervalo = findViewById(R.id.editIntervalo);
        editDuracao = findViewById(R.id.editDuracao);
        txtHorarioSelecionado = findViewById(R.id.txtHorarioSelecionado);
        spinnerAdm = findViewById(R.id.spinnerAdm);

        btnAdicionarMed = findViewById(R.id.btnAdicionarMed);
        btnAtualizarMed = findViewById(R.id.btnAtualizarMed);
        btnExcluirMed = findViewById(R.id.btnExcluirMed);

        get_Intent = getIntent();

        // Por padrão, desativa os botões de atualização e exclusão
        btnAtualizarMed.setEnabled(false);
        btnExcluirMed.setEnabled(false);

        // Verifica se estamos no modo de edição
        boolean edicao = get_Intent.getBooleanExtra("edicao_Med", false);
        if (edicao) {
            carregarTarefa(); // Preenche os campos com os dados recebidos
            btnAdicionarMed.setEnabled(false); // Desativa o botão de adicionar
            btnAtualizarMed.setEnabled(true);  // Ativa o botão de atualizar
            btnExcluirMed.setEnabled(true);    // Ativa o botão de excluir
        } else {
            btnAtualizarMed.setEnabled(false);
            btnExcluirMed.setEnabled(false);
        }


        btnAdicionarMed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    // Obter os dados dos campos de texto
                    String nomeMed = editNomeMed.getText().toString();
                    String descMed = editDesc.getText().toString();
                    String doseMed = editDose.getText().toString();
                    String horario = txtHorarioSelecionado.getText().toString();
                    String admMed = spinnerAdm.getSelectedItem().toString();
                    // Verificar se os campos estão preenchidos
                    if (nomeMed.isEmpty() || descMed.isEmpty() || doseMed.isEmpty()
                            || horario.isEmpty() || editIntervalo.getText().toString().isEmpty()
                            || editDuracao.getText().toString().isEmpty()) {
                        Toast.makeText(Tela_Editar_Medicamento.this, "Preencha todos os " +
                                "campos corretamente!", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    Integer intervalo_horas;
                    Integer duracao_dias;


                    try {
                        intervalo_horas = Integer.parseInt(editIntervalo.getText().toString());
                        duracao_dias = Integer.parseInt(editDuracao.getText().toString());
                    } catch (NumberFormatException e) {
                        Toast.makeText(Tela_Editar_Medicamento.this, "Intervalo ou " +
                                "duração inválidos!", Toast.LENGTH_SHORT).show();
                        return;
                    }


                    _AdicionarMedicamento(nomeMed, descMed, doseMed, horario, admMed, intervalo_horas, duracao_dias);

                    Intent resultadoIntent = new Intent();
                    setResult(RESULT_OK, resultadoIntent); // Enviando os dados de volta
                    finish();
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(Tela_Editar_Medicamento.this, "Erro: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });


        btnAtualizarMed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    // Atualiza o medicamento com os novos dados

                    String dataHoraAtt = txtHorarioSelecionado.getText().toString();
                    Integer id = get_Intent.getIntExtra("id", 0);

                    _AtualizarMedicamento(dataHoraAtt, id);

                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(Tela_Editar_Medicamento.this, "Erro ao atualizar o medicamento!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnExcluirMed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    // Obter o ID do medicamento a ser excluído
                    Integer id = get_Intent.getIntExtra("id", 0);
                    _ConfirmarExclusao(id); // Exclui o medicamento
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(Tela_Editar_Medicamento.this, "Erro ao excluir o medicamento!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    public void carregarTarefa() {
        get_Intent = getIntent();
        boolean editar = get_Intent.getBooleanExtra("edicao_Med", false);

        if (editar) {
            try {
                // Corrigir os nomes dos extras
                String nomeMed = get_Intent.getStringExtra("NomeMedicamento");
                String dataHora = get_Intent.getStringExtra("dataHora");
                String status = get_Intent.getStringExtra("status");
                String admMed = get_Intent.getStringExtra("admMedicamento");
                String descMed = get_Intent.getStringExtra("descricaoMedicamento");
                String doseMed = get_Intent.getStringExtra("doseMedicamento");
                String intervalo_horas = get_Intent.getStringExtra("intervalo_horas");
                String duracao_dias = get_Intent.getStringExtra("duracao_dias");


                int id = get_Intent.getIntExtra("id", 0);

                // Preencher os campos
                editNomeMed.setText(nomeMed);
                txtHorarioSelecionado.setText(dataHora);
                editDesc.setText(descMed);
                editDose.setText(doseMed);
                editIntervalo.setText(intervalo_horas);
                editDuracao.setText(duracao_dias);

                // Preencher o Spinner, se necessário
                if (spinnerAdm != null && admMed != null) {
                    for (int i = 0; i < spinnerAdm.getCount(); i++) {
                        if (spinnerAdm.getItemAtPosition(i).toString().equalsIgnoreCase(admMed)) {
                            spinnerAdm.setSelection(i);
                            break;
                        }
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "Erro ao carregar os dados do medicamento.", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void carregarBancoDeDados() {
        dbHelper = new DatabaseHelper(this);
        bancoDeDados = dbHelper.getWritableDatabase(); // Abre o banco para leitura e escrita
    }

    private void _AdicionarMedicamento(String nomeMed, String descMed, String doseMed,
                                       String dataHora, String admMed, Integer intervalo_horas,
                                       Integer duracao_dias) {
        SQLiteStatement stmt = null;
        try {
//            String admMed = itens_admMed.toString();

            String sql = "INSERT INTO medicamentos (nomeMedicamento, dataHora, status, " +
                    "admMedicamento, descricao, dose, intervalo_horas, " +
                    "duracao_dias) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            stmt = bancoDeDados.compileStatement(sql);
            stmt.bindString(1, nomeMed); // nome
            stmt.bindString(2, dataHora); // data e hora
            stmt.bindString(3, "1"); //status de ativo
            stmt.bindString(4, admMed); // administração do medicamento
            stmt.bindString(5, descMed); // descrição do medicamento
            stmt.bindString(6, doseMed); // dose do medicamento
            stmt.bindLong(7, intervalo_horas); // intervalo de horas
            stmt.bindLong(8, duracao_dias); // duração em dias


            long id = stmt.executeInsert();

            Toast.makeText(Tela_Editar_Medicamento.this, "Medicamento adicionado " +
                    "com sucesso! Com id=" + id, Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(Tela_Editar_Medicamento.this, "Erro ao adicionar " +
                    "o medicamento! Não é no Botão Adicionar", Toast.LENGTH_SHORT).show();
        } finally {
            if (stmt != null) {
                stmt.close();
            }

        }

    }

    private void _AtualizarMedicamento(String DataHoraAtt, Integer id) {
        SQLiteStatement stmt = null;
        String status = get_Intent.getStringExtra("status");

        String nomeMed = editNomeMed.getText().toString();
        String admMed = spinnerAdm.getSelectedItem().toString();
        String descMed = editDesc.getText().toString();
        String doseMed = editDose.getText().toString();
        Integer intervalo_horas = Integer.parseInt(editIntervalo.getText().toString());
        Integer duracao_dias = Integer.parseInt(editDuracao.getText().toString());

        if (status == null) status = "1";

        Intent resultadoIntent = new Intent();
        try {
            //Usando o SQLiteStatement para segurança e eficiência
            String sql = "UPDATE medicamentos SET nomeMedicamento = ?, dataHora = ?, status = ?, " +
                    "admMedicamento = ?, descricao = ?, dose =?, intervalo_horas = ?, " +
                    "duracao_dias = ? WHERE id = ?";
            stmt = bancoDeDados.compileStatement(sql);
            stmt.bindString(1, nomeMed);
            stmt.bindString(2, DataHoraAtt);
            stmt.bindString(3, status);
            stmt.bindString(4, admMed);
            stmt.bindString(5, descMed);
            stmt.bindString(6, doseMed);
            stmt.bindLong(7, intervalo_horas);
            stmt.bindLong(8, duracao_dias);
            stmt.bindLong(9, id);

            stmt.executeUpdateDelete();
            Toast.makeText(Tela_Editar_Medicamento.this, "Medicamento " +
                    "atualizado com sucesso!", Toast.LENGTH_SHORT).show();

            setResult(RESULT_OK, resultadoIntent); // Enviando os dados de volta
            finish();
        } catch (Exception e) {
            e.printStackTrace();
            // Aqui você pode adicionar um Toast de erro se desejar
            Toast.makeText(this, "Erro ao atualizar a tarefa!",
                    Toast.LENGTH_SHORT).show();
        } finally {
            if (stmt != null) {
                stmt.close();
            }
        }
    }

    private void _ConfirmarExclusao(final Integer id) {
        // Criar um AlertDialog para confirmação
        new AlertDialog.Builder(Tela_Editar_Medicamento.this).setTitle("Confirmar " +
                "Exclusão").setMessage("Você tem certeza que deseja excluir este " +
                "medicamento?").setPositiveButton("Sim", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent resultadoIntent = new Intent();
                // Caso o usuário confirme, chama o método de exclusão
                _ExcluirMedicamento(id);

                setResult(RESULT_OK, resultadoIntent); // Passa a resposta de sucesso para a atividade anterior
                finish(); // Finaliza a atividade após a exclusão
            }
        }).setNegativeButton("Não", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Se o usuário cancelar, não faz nada
                dialog.dismiss();
            }
        }).create().show();
    }

    public void _selecionarHorario(View view) {
        Calendar calendar = Calendar.getInstance();
        int hora = calendar.get(Calendar.HOUR_OF_DAY);
        int minuto = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(this, (timePicker, selectedHour, selectedMinute) -> {
            TextView txtHora = findViewById(R.id.txtHorarioSelecionado);
            txtHora.setText(String.format(Locale.getDefault(), "%02d:%02d", selectedHour, selectedMinute));
        }, hora, minuto, true);

        timePickerDialog.show();
    }

    private void _ExcluirMedicamento(Integer id) {
        SQLiteStatement stmt = null;
        try {
            // Usando o SQLiteStatement para segurança e eficiência
            String sql = "DELETE FROM medicamentos WHERE id = ?";
            stmt = bancoDeDados.compileStatement(sql);
            stmt.bindLong(1, id); // Vincula o id ao medicamento a ser excluído

            int rowsDeleted = stmt.executeUpdateDelete(); // Executa a exclusão

            if (rowsDeleted > 0) {
                Toast.makeText(Tela_Editar_Medicamento.this, "Medicamento excluído " +
                        "com sucesso!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(Tela_Editar_Medicamento.this, "Erro ao excluir o " +
                        "Medicamento!", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(Tela_Editar_Medicamento.this, "Erro ao excluir o " +
                    "Medicamento!", Toast.LENGTH_SHORT).show();
        } finally {
            if (stmt != null) {
                stmt.close();
            }
        }
    }
}