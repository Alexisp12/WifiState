package com.blp.outilcartographique.wifistate;

/**
 * Created by Alexis on 19/05/2017.
 */

        import android.app.Activity;
        import android.app.AlertDialog;
        import android.content.DialogInterface;
        import android.content.Intent;
        import android.content.SharedPreferences;
        import android.os.Bundle;
        import android.os.Environment;
        import android.view.View;
        import android.widget.Button;
        import android.widget.EditText;
        import android.widget.RadioButton;
        import android.widget.RadioGroup;
        import android.widget.SeekBar;
        import android.widget.TextView;
        import android.widget.Toast;

        import java.io.File;

        import static com.blp.outilcartographique.wifistate.Accueil.continuebool;
        import static com.blp.outilcartographique.wifistate.Accueil.discontinuebool;


/**
 * Created by Alexis on 30/01/2017.
 */

public class Parametre_acquisition extends Activity {
    private Button boutonEnregistrer;
    final String NOMBREECHANTILLONTOTAL ="nombreEchantillonTotal";
    final String NOMFICHIERSTRING ="nomFichierString";
    final String NOMCAMPAGNESTRING ="nomCampagneString";
    private int nombreEchantillonTotal;
    private String nomFichierString;
    public static String SETS; // Nom de la sauvegarde des préférences

    private SeekBar.OnSeekBarChangeListener SeekBarListener;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.parametre_acquisition);
        final SeekBar seekBar;
        final EditText fichierString;
        final EditText nombreEchantillon;
        final RadioGroup radioGroup=(RadioGroup) findViewById(R.id.radioGroup);
        final RadioButton radiobuttonContinue =(RadioButton)findViewById(R.id.radio_continue);
        final RadioButton radiobuttonDiscontinue =(RadioButton)findViewById(R.id.radio_discontinue);
        final TextView nbEchantillonTextView = (TextView) findViewById(R.id.TextView2);

        SharedPreferences settings = getSharedPreferences(SETS, 0);
        discontinuebool = settings.getBoolean("discontinuebool", discontinuebool);
        continuebool = settings.getBoolean("continuebool", continuebool);

        Intent intent =getIntent();
        nombreEchantillonTotal=intent.getIntExtra(NOMBREECHANTILLONTOTAL,0);
        nomFichierString=intent.getStringExtra(NOMFICHIERSTRING);

        boutonEnregistrer = (Button) findViewById(R.id.enregistrer);
        fichierString = (EditText) findViewById(R.id.EditText1);
        nombreEchantillon = (EditText) findViewById(R.id.EditText2);
        seekBar = (SeekBar) findViewById(R.id.seekBar);


        if(discontinuebool){
            radiobuttonDiscontinue.setChecked(true);
            seekBar.setVisibility(View.VISIBLE);
            nbEchantillonTextView.setVisibility(View.VISIBLE);
            nombreEchantillon.setVisibility(View.VISIBLE);
            seekBar.setProgress(nombreEchantillonTotal);

            SeekBarListener =
                    new SeekBar.OnSeekBarChangeListener() {
                        @Override
                        public void onProgressChanged (SeekBar seekBar, int progress, boolean fromUser) {
                            nombreEchantillonTotal=progress;
                            nombreEchantillon.setText(Integer.toString(nombreEchantillonTotal));
                        }

                        @Override
                        public void onStartTrackingTouch(SeekBar seekBar) {

                        }

                        @Override
                        public void onStopTrackingTouch(SeekBar seekBar) {

                        }
                    };

            seekBar.setOnSeekBarChangeListener(SeekBarListener);
            nombreEchantillon.setText(Integer.toString(nombreEchantillonTotal+1));
        }
        if(continuebool){
            radiobuttonContinue.setChecked(true);
            seekBar.setVisibility(View.GONE);
            nbEchantillonTextView.setVisibility(View.GONE);
            nombreEchantillon.setVisibility(View.GONE);

        }

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch(checkedId) {
                    case R.id.radio_discontinue :
                        discontinuebool=true;
                        continuebool=false;

                        seekBar.setVisibility(View.VISIBLE);
                        nbEchantillonTextView.setVisibility(View.VISIBLE);
                        nombreEchantillon.setVisibility(View.VISIBLE);

                        seekBar.setProgress(nombreEchantillonTotal);

                        SeekBarListener =
                                new SeekBar.OnSeekBarChangeListener() {
                                    @Override
                                    public void onProgressChanged (SeekBar seekBar, int progress, boolean fromUser) {
                                        nombreEchantillonTotal=progress;
                                        nombreEchantillon.setText(Integer.toString(nombreEchantillonTotal));
                                    }

                                    @Override
                                    public void onStartTrackingTouch(SeekBar seekBar) {

                                    }

                                    @Override
                                    public void onStopTrackingTouch(SeekBar seekBar) {

                                    }
                                };

                        seekBar.setOnSeekBarChangeListener(SeekBarListener);
                        nombreEchantillon.setText(Integer.toString(nombreEchantillonTotal+1));

                        break;
                    case R.id.radio_continue :
                        nombreEchantillon.setVisibility(View.GONE);
                        seekBar.setVisibility(View.GONE);
                        nbEchantillonTextView.setVisibility(View.GONE);
                        continuebool=true;
                        discontinuebool=false;
                        break;
                    default:
                        break;
                }
            }

        });



        fichierString.setText(nomFichierString);

        boutonEnregistrer.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                SharedPreferences settings = getSharedPreferences(SETS, 0);
                SharedPreferences.Editor editor = settings.edit();

                if(nombreEchantillon.getText().length()==0){
                    // No change
                } else {
                    nombreEchantillonTotal = (Integer.parseInt(nombreEchantillon.getText().toString())) - 1;
                    editor.putInt("nombreEchantillonTotal", nombreEchantillonTotal);
                    editor.putBoolean("continuebool",continuebool);
                    editor.putBoolean("discontinuebool",discontinuebool);
                    editor.commit();
                }

                if(fichierString.getText().length() == 0) {
                    // No change
                } else {

                    File mesures = new File(Environment.getExternalStorageDirectory() + File.separator + "WifiStateData", nomFichierString);

                    if(mesures.renameTo(new File(Environment.getExternalStorageDirectory() + File.separator + "WifiStateData", fichierString.getText().toString())) || (fichierString.getText().toString()).equals(nomFichierString)){
                        editor.putString("nomFichier",  fichierString.getText().toString());
                        editor.commit();
                        if(discontinuebool) {
                            Toast toast = Toast.makeText(getApplicationContext(),"Mode obsolète", Toast.LENGTH_SHORT);
                            toast.show();
                        }
                        if(continuebool){
                            editor.putBoolean("continuebool",continuebool);
                            editor.putBoolean("discontinuebool",discontinuebool);
                            editor.commit();
                            Intent intent2 = new Intent(Parametre_acquisition.this, Acquisition_mesure_continue.class);
                            startActivity(intent2);
                        }
                    } else {
                        AlertDialog.Builder alertdial = new AlertDialog.Builder(Parametre_acquisition.this);
                        alertdial.setTitle("Impossible de renommer la campagne");
                        alertdial.setMessage("Vérifiez qu'un fichier portant ce nom n'éxiste pas déjà "); //+fichierString.getText().toString()+" "+nomCampagneString
                        alertdial.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                        alertdial.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                        alertdial.setIcon(android.R.drawable.ic_dialog_alert);
                        alertdial.show();
                    }
                }
            }
        });
    }

}



