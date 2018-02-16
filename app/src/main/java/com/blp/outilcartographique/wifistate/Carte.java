package com.blp.outilcartographique.wifistate;

/**
 * Created by Alexis on 19/05/2017.
 */

        import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.SnapshotReadyCallback;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static android.view.Gravity.BOTTOM;
import static android.view.Gravity.LEFT;
import static com.blp.outilcartographique.wifistate.Accueil.Seuil1;
import static com.blp.outilcartographique.wifistate.Accueil.Seuil2;
import static com.blp.outilcartographique.wifistate.Accueil.Seuil3;
import static com.blp.outilcartographique.wifistate.Accueil.Seuil4;
import static com.blp.outilcartographique.wifistate.Accueil.Seuil5;
import static com.blp.outilcartographique.wifistate.Accueil.SeuilPasDeValeur;
import static com.blp.outilcartographique.wifistate.Accueil.accueilActif;
import static com.blp.outilcartographique.wifistate.Accueil.carteActif;
import static com.blp.outilcartographique.wifistate.Accueil.choixCanal;
import static com.blp.outilcartographique.wifistate.Accueil.choixMac;
import static com.blp.outilcartographique.wifistate.Accueil.choixMacReseauExterne;
import static com.blp.outilcartographique.wifistate.Accueil.choixSSID;
import static com.blp.outilcartographique.wifistate.Accueil.contextActif;
import static com.blp.outilcartographique.wifistate.Accueil.nbPrediction;
import static com.blp.outilcartographique.wifistate.Accueil.switchCanalActif;
import static com.blp.outilcartographique.wifistate.Accueil.switchMACActif;
import static com.blp.outilcartographique.wifistate.Acquisition_mesure_continue.nomFichierString;
import static com.blp.outilcartographique.wifistate.Prediction.MessagePrediction;
import static com.blp.outilcartographique.wifistate.Prediction.casesParcouruesSansPrediction;
import static com.blp.outilcartographique.wifistate.Prediction.nbCasesPredites;
import static com.blp.outilcartographique.wifistate.Prediction.predibool1;
import static com.blp.outilcartographique.wifistate.Prediction.predibool2;
import static com.blp.outilcartographique.wifistate.Prediction.razCasesParcouruesSansPrediction;
import static com.blp.outilcartographique.wifistate.Traitement.LENGTH_MAX_VALUE;
import static com.blp.outilcartographique.wifistate.Traitement.choixMAC1boolean;
import static com.blp.outilcartographique.wifistate.Traitement.choixSSID2boolean;
import static com.blp.outilcartographique.wifistate.Traitement.couleurAleatoire;
import static com.blp.outilcartographique.wifistate.Traitement.indiceCanalPredominant;
import static com.blp.outilcartographique.wifistate.Traitement.indiceMacPredominant;

/**
 * Created by Alexis on 05/01/2017.
 */

public class Carte extends FragmentActivity implements OnMapReadyCallback {
    public static boolean choixMacMAJBoolean;
    private boolean modeIndoor;
    private boolean modeOutdoor;
    private boolean modePerso;
    public boolean modifOption= false;
    private boolean radio_level_bool=false;
    private boolean radio_mac_bool=false;
    private boolean radio_canaux_bool=false;
    private Button predireButton;
    private Button modifSeuils;
    private Button modifMAC;
    private Button modifGranularite;
    private Button legendeButton;
    private Button screenButton;
    private Button switchCarte;
    private static TextView txt0 ;    //Contient la première ligne de la légende, un string de couleur inchangée
    private static TextView txt1 ;   //Contient la seconde ligne de la légende, un string de la couleur de la case correspondante à l'adresse MAC/canal sélectionné
    public static TextView prediTextView;
    private TextView nomCampagneView;
    private TextView nomSSIDView;
    private TextView prediction;
    private TextView passages;
    private TextView Seuil1View;
    private TextView nbPredictionView;
    private TextView Seuil2View;
    private TextView Seuil3View;
    private TextView Seuil4View;
    private RelativeLayout seuilsRelativeLayout;
    private RelativeLayout optionAffichageRL;
    private RelativeLayout predictionRelativeLayout;
    private int ValSeuil1=-70;
    private int ValSeuil2=-75;
    private int ValSeuil3=-80;
    private int ValSeuil4=-85;
    private int ValSeuil5= 10;
    public static int nbCasesParcouruesInit;
    private LayoutInflater alertdialogModifSeuils;
    private LayoutInflater alertdialogChoixMac;
    private LayoutInflater alertdialogModifGranularite;
    protected ProgressDialog mProgressDialog;
    public Switch switchAffichageLegende;
    private final String EXTRA_SEUIL1 = "seuil1";
    private final String EXTRA_SEUIL2 = "seuil2";
    private final String EXTRA_SEUIL3 = "seuil3";
    private final String EXTRA_SEUIL4 = "seuil4";
    private Accueil.ErrorStatus status;
    public static final int MSG_ERR = 0;
    public static final int MSG_CNF = 1;
    public static final int MSG_IND = 2;
    public static AlertDialog.Builder legendAlertDialog;  // Constructeur de de l'alertdialog personnalisé

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.carte);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        switchCarte=(Button) findViewById(R.id.go);
        carteActif = true;
        accueilActif = false;
        contextActif = getApplicationContext();

        // RadioGroup
        final RadioGroup radioGroup=(RadioGroup) findViewById(R.id.radioGroup);
        final RadioButton radio_level =(RadioButton) findViewById(R.id.radio_level);
        final RadioButton radio_mac =(RadioButton) findViewById(R.id.radio_mac);
        final RadioButton radio_canaux =(RadioButton) findViewById(R.id.radio_canaux);

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch(checkedId) {
                    case R.id.radio_level :
                        radio_level_bool=true;
                        radio_mac_bool=false;
                        radio_canaux_bool=false;

                        modifOption=true;
                        break;
                    case R.id.radio_mac :
                        radio_level_bool=false;
                        radio_mac_bool=true;
                        radio_canaux_bool=false;
                        modifOption=true;
                        break;
                    case R.id.radio_canaux :
                        radio_level_bool=false;
                        radio_mac_bool=false;
                        radio_canaux_bool=true;

                        modifOption=true;
                        break;
                    default:
                        break;
                }
            }

        });

        String path = Environment.getExternalStorageDirectory() + File.separator + "WifiStateData" + File.separator + "Captures d'écran ";
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }

        nbPredictionView = (TextView) findViewById(R.id.nbPrediction);
        nbPredictionView.setText(Integer.toString(nbPrediction));
        predireButton = (Button) findViewById(R.id.actualiser);
        legendeButton = (Button) findViewById(R.id.legendButton);
        screenButton = (Button) findViewById(R.id.screenButton);
        modifSeuils = (Button) findViewById(R.id.modifSeuils);
        modifGranularite= (Button) findViewById(R.id.modifGranularité);
        modifMAC=(Button) findViewById(R.id.modifMAC);
        //modifCanaux=(Button) findViewById(R.id.modifCanaux);
        prediction = (TextView) findViewById(R.id.prediction);
        passages = (TextView) findViewById(R.id.passages);

        //Ajout SeuilView 23/03
        // Affectation textView du seuil4
        Seuil4View = (TextView) findViewById(R.id.textView6);
        Seuil4View.setText(" "+Integer.toString(Seuil4)+" dB < ");

        // Affectation textView du seuil3
        Seuil3View = (TextView) findViewById(R.id.textView7);
        Seuil3View.setText(" < "+Integer.toString(Seuil3)+" dB < ");

        // Affectation textView du seuil2
        Seuil2View = (TextView) findViewById(R.id.textView8);
        Seuil2View.setText(" < "+Integer.toString(Seuil2)+" dB < ");


        // Affectation textView du seuil1
        Seuil1View = (TextView) findViewById(R.id.textView9);
        Seuil1View.setText(" < "+Integer.toString(Seuil1)+" dB < ");
        // Relative Layout
        seuilsRelativeLayout = (RelativeLayout) findViewById(R.id.Seuils);
        predictionRelativeLayout = (RelativeLayout) findViewById(R.id.predictionRelativeLayout);
        optionAffichageRL = (RelativeLayout) findViewById(R.id.optionAffichageRL);
        prediTextView = (TextView) findViewById(R.id.prediText);

        switchAffichageLegende = (Switch) findViewById(R.id.mySwitch);
        //set the switch to ON
        switchAffichageLegende.setChecked(true);
        //attach a listener to check for changes in state
        switchAffichageLegende.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {

                if(isChecked){
                    seuilsRelativeLayout.setVisibility(View.VISIBLE);
                    predictionRelativeLayout.setVisibility(View.VISIBLE);
                    optionAffichageRL.setVisibility(View.VISIBLE);
                    if(nbPrediction>0) {
                        prediTextView.setVisibility(View.VISIBLE);
                    } else {
                        prediTextView.setVisibility(View.GONE);
                    }
                    modifGranularite.setVisibility(View.VISIBLE);
                    modifMAC.setVisibility(View.VISIBLE);
                    modifSeuils.setVisibility(View.VISIBLE);

                }else{
                    seuilsRelativeLayout.setVisibility(View.GONE);
                    predictionRelativeLayout.setVisibility(View.GONE);
                    optionAffichageRL.setVisibility(View.GONE);
                    modifGranularite.setVisibility(View.GONE);
                    modifSeuils.setVisibility(View.GONE);
                    modifMAC.setVisibility(View.GONE);
                }

            }
        });


        //check the current state before we display the screen
        if(switchAffichageLegende.isChecked()){
            if(nbPrediction>0) {
                prediTextView.setVisibility(View.VISIBLE);
            } else {
                prediTextView.setVisibility(View.GONE);
            }
            prediction.setVisibility(View.VISIBLE);
            nbPredictionView.setVisibility(View.VISIBLE);
            passages.setVisibility(View.VISIBLE);
            seuilsRelativeLayout.setVisibility(View.VISIBLE);
        } else {
            seuilsRelativeLayout.setVisibility(View.GONE);
            prediTextView.setVisibility(View.GONE);
            prediction.setVisibility(View.GONE);
            nbPredictionView.setVisibility(View.GONE);
            passages.setVisibility(View.GONE);
        }

        //Fin ajout du 25/03

        // Affectation textView
        nomCampagneView = (TextView) findViewById(R.id.nomCampagne);
        nomCampagneView.setText(nomFichierString);

        nomSSIDView = (TextView) findViewById(R.id.nomSSID);
        nomSSIDView.setText(choixSSID);

        if(switchMACActif || switchCanalActif){
            if(switchMACActif){
                radio_mac.setChecked(true);

            }
            if(switchCanalActif){
                radio_canaux.setChecked(true);

            }
            seuilsRelativeLayout.setVisibility(View.GONE);
            predictionRelativeLayout.setVisibility(View.GONE);
            predireButton.setVisibility(View.GONE);
            legendeButton.setVisibility(View.VISIBLE);

        } else {
            seuilsRelativeLayout.setVisibility(View.VISIBLE);
            predictionRelativeLayout.setVisibility(View.VISIBLE);
            legendeButton.setVisibility(View.GONE);
            predireButton.setVisibility(View.VISIBLE);
            radio_level.setChecked(true);
        }
        alertdialogModifSeuils = LayoutInflater.from(this);
        alertdialogChoixMac = LayoutInflater.from(this);
        alertdialogModifGranularite = LayoutInflater.from(this);
    }


    @Override
    public void onMapReady(final GoogleMap map) {
        map.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        Acquisition_mesure_continue.coordGPSUpdated=false;

        if(predibool1|| predibool2) {
            Prediction.TextPrediction();
            prediTextView.setText(MessagePrediction);
            predibool1 = false;
        }

        Log.d("prediboolOnCreate",Boolean.toString(predibool1));

        final SnapshotReadyCallback callback = new SnapshotReadyCallback() {
            Bitmap bitmap;

            @Override
            public void onSnapshotReady(Bitmap snapshot) {
                bitmap = snapshot;
                Date now = new Date();
                android.text.format.DateFormat.format("yyyy-MM_hh:mm", now);
                try {

                    String path2 = Environment.getExternalStorageDirectory() + File.separator + "WifiStateData" + File.separator + "Captures d'écran ";
                    File file2 = new File(path2);
                    if (!file2.exists()) {
                        file2.mkdirs();
                    }

                    //String mPath = Environment.getExternalStorageDirectory() + File.separator + "WifiStateData" + File.separator + "Captures d'écran "+ nomCampagneString;
                    FileOutputStream out = new FileOutputStream(path2 + "/ "+ now + ".png");
                    bitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
                    Toast.makeText(Carte.this, "Capture effectuée", Toast.LENGTH_LONG).show();
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(Carte.this, "Capture échouée", Toast.LENGTH_LONG).show();
                }
            }
        };

        screenButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View View) {
                map.snapshot(callback);
            }
        });


        map.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                LatLng SudOuest = new LatLng(Traitement.getLatMin(), Traitement.getLongMin());
                LatLng NordEst = new LatLng(Traitement.getLatMax(), Traitement.getLongMax());
                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                builder.include(SudOuest);
                builder.include(NordEst);
                map.moveCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 17));
                // map.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 30));
            }
        });


        generationPolygon(map);

        modifSeuils.setOnClickListener(new View.OnClickListener() {
            public void onClick(final View View) {
                final View alertDialogViewModifSeuils = alertdialogModifSeuils.inflate(R.layout.alertdialog_modif_seuils, null);
                final AlertDialog.Builder alertdial = new AlertDialog.Builder(Carte.this);
                alertdial.setView(alertDialogViewModifSeuils);
                alertdial.setTitle("Modification seuils");

                /* Déclaration des seekBarListener */
                SeekBar.OnSeekBarChangeListener SeekBarListener1 =
                        new SeekBar.OnSeekBarChangeListener() {
                            @Override
                            public void onProgressChanged (SeekBar seekBar, int progress, boolean fromUser) {
                                final SeekBar SeekBar2;
                                final SeekBar SeekBar3;
                                final SeekBar SeekBar4;
                                final EditText EditSeuil1;
                                final EditText EditSeuil2;
                                final EditText EditSeuil3;
                                final EditText EditSeuil4;

                                //Seek Bar
                                SeekBar2 = (SeekBar) alertDialogViewModifSeuils.findViewById(R.id.seekBar2);
                                SeekBar3 = (SeekBar) alertDialogViewModifSeuils.findViewById(R.id.seekBar3);
                                SeekBar4 = (SeekBar) alertDialogViewModifSeuils.findViewById(R.id.seekBar4);

                                EditSeuil1 = (EditText) alertDialogViewModifSeuils.findViewById(R.id.EditSeuil1);
                                EditSeuil2 = (EditText) alertDialogViewModifSeuils.findViewById(R.id.EditSeuil2);
                                EditSeuil3 = (EditText) alertDialogViewModifSeuils.findViewById(R.id.EditSeuil3);
                                EditSeuil4 = (EditText) alertDialogViewModifSeuils.findViewById(R.id.EditSeuil4);

                                if(ValSeuil1>=ValSeuil2) {
                                    ValSeuil1 = -progress;
                                    EditSeuil1.setText(Integer.toString(ValSeuil1));
                                } else {
                                    if(ValSeuil2>=ValSeuil3){
                                        ValSeuil1 = -progress;
                                        EditSeuil1.setText(Integer.toString(ValSeuil1));

                                        ValSeuil2 = ValSeuil1;
                                        SeekBar2.setProgress(-ValSeuil2);
                                        EditSeuil2.setText(Integer.toString(ValSeuil2));

                                    } else {
                                        if (ValSeuil3>=ValSeuil4) {
                                            ValSeuil1 = -progress;
                                            EditSeuil1.setText(Integer.toString(ValSeuil1));

                                            ValSeuil2 = ValSeuil1;
                                            SeekBar2.setProgress(-ValSeuil2);
                                            EditSeuil2.setText(Integer.toString(ValSeuil2));

                                            ValSeuil3 = ValSeuil2;
                                            SeekBar3.setProgress(-ValSeuil3);
                                            EditSeuil3.setText(Integer.toString(ValSeuil3));

                                        } else {
                                            ValSeuil1 = -progress;
                                            EditSeuil1.setText(Integer.toString(ValSeuil1));

                                            ValSeuil2 = ValSeuil1;
                                            SeekBar2.setProgress(-ValSeuil2);
                                            EditSeuil2.setText(Integer.toString(ValSeuil2));

                                            ValSeuil3 = ValSeuil2;
                                            SeekBar3.setProgress(-ValSeuil3);
                                            EditSeuil3.setText(Integer.toString(ValSeuil3));

                                            ValSeuil4 = ValSeuil3;
                                            SeekBar4.setProgress(-ValSeuil4);
                                            EditSeuil4.setText(Integer.toString(ValSeuil4));
                                        }
                                    }
                                }
                            }

                            @Override
                            public void onStartTrackingTouch(SeekBar seekBar) {

                            }

                            @Override
                            public void onStopTrackingTouch(SeekBar seekBar) {

                            }
                        };
                SeekBar.OnSeekBarChangeListener SeekBarListener2 =
                        new SeekBar.OnSeekBarChangeListener() {
                            @Override
                            public void onProgressChanged (SeekBar seekBar, int progress, boolean fromUser) {
                                final SeekBar SeekBar2;
                                final SeekBar SeekBar3;
                                final SeekBar SeekBar4;
                                final SeekBar SeekBar1;
                                final EditText EditSeuil1;
                                final EditText EditSeuil2;
                                final EditText EditSeuil3;
                                final EditText EditSeuil4;

                                //Seek Bar
                                SeekBar1 = (SeekBar) alertDialogViewModifSeuils.findViewById(R.id.seekBar1);
                                SeekBar2 = (SeekBar) alertDialogViewModifSeuils.findViewById(R.id.seekBar2);
                                SeekBar3 = (SeekBar) alertDialogViewModifSeuils.findViewById(R.id.seekBar3);
                                SeekBar4 = (SeekBar) alertDialogViewModifSeuils.findViewById(R.id.seekBar4);

                                EditSeuil1 = (EditText) alertDialogViewModifSeuils.findViewById(R.id.EditSeuil1);
                                EditSeuil2 = (EditText) alertDialogViewModifSeuils.findViewById(R.id.EditSeuil2);
                                EditSeuil3 = (EditText) alertDialogViewModifSeuils.findViewById(R.id.EditSeuil3);
                                EditSeuil4 = (EditText) alertDialogViewModifSeuils.findViewById(R.id.EditSeuil4);


                                // user has moved the seekBar which has change the progress value
                                if(ValSeuil2>=ValSeuil3 && ValSeuil2 <= ValSeuil1 ){
                                    ValSeuil2 = -progress;
                                    SeekBar2.setProgress(-ValSeuil2);
                                    EditSeuil2.setText(Integer.toString(ValSeuil2));

                                } else {
                                    if(ValSeuil2<ValSeuil3) {
                                        if (ValSeuil3 > ValSeuil4) {
                                            ValSeuil2 = -progress;
                                            SeekBar2.setProgress(-ValSeuil2);
                                            EditSeuil2.setText(Integer.toString(ValSeuil2));

                                            ValSeuil3 = -progress;
                                            SeekBar3.setProgress(-ValSeuil3);
                                            EditSeuil3.setText(Integer.toString(ValSeuil3));

                                        } else {
                                            ValSeuil2 = -progress;
                                            SeekBar2.setProgress(-ValSeuil2);
                                            EditSeuil2.setText(Integer.toString(ValSeuil2));

                                            ValSeuil3 = -progress;
                                            SeekBar3.setProgress(-ValSeuil3);
                                            EditSeuil3.setText(Integer.toString(ValSeuil3));

                                            ValSeuil4 = -progress;
                                            SeekBar4.setProgress(-ValSeuil4);
                                            EditSeuil4.setText(Integer.toString(ValSeuil4));
                                        }
                                    }

                                    if(ValSeuil2>ValSeuil1){
                                        ValSeuil2 = -progress;
                                        SeekBar2.setProgress(-ValSeuil2);
                                        EditSeuil2.setText(Integer.toString(ValSeuil2));

                                        ValSeuil1 = -progress;
                                        SeekBar1.setProgress(-ValSeuil1);
                                        EditSeuil1.setText(Integer.toString(ValSeuil1));
                                    }
                                }
                            }

                            @Override
                            public void onStartTrackingTouch(SeekBar seekBar) {

                            }

                            @Override
                            public void onStopTrackingTouch(SeekBar seekBar) {

                            }
                        };
                SeekBar.OnSeekBarChangeListener SeekBarListener3 =
                        new SeekBar.OnSeekBarChangeListener() {

                            @Override
                            public void onProgressChanged (SeekBar seekBar, int progress, boolean fromUser) {
                                final SeekBar SeekBar2;
                                final SeekBar SeekBar3;
                                final SeekBar SeekBar4;
                                final SeekBar SeekBar1;
                                final EditText EditSeuil1;
                                final EditText EditSeuil2;
                                final EditText EditSeuil3;
                                final EditText EditSeuil4;

                                //Seek Bar
                                SeekBar1 = (SeekBar) alertDialogViewModifSeuils.findViewById(R.id.seekBar1);
                                SeekBar2 = (SeekBar) alertDialogViewModifSeuils.findViewById(R.id.seekBar2);
                                SeekBar3 = (SeekBar) alertDialogViewModifSeuils.findViewById(R.id.seekBar3);
                                SeekBar4 = (SeekBar) alertDialogViewModifSeuils.findViewById(R.id.seekBar4);

                                EditSeuil1 = (EditText) alertDialogViewModifSeuils.findViewById(R.id.EditSeuil1);
                                EditSeuil2 = (EditText) alertDialogViewModifSeuils.findViewById(R.id.EditSeuil2);
                                EditSeuil3 = (EditText) alertDialogViewModifSeuils.findViewById(R.id.EditSeuil3);
                                EditSeuil4 = (EditText) alertDialogViewModifSeuils.findViewById(R.id.EditSeuil4);

                                if (ValSeuil3 >= ValSeuil4 && ValSeuil3 <= ValSeuil2) {
                                    ValSeuil3 = -progress;
                                    SeekBar3.setProgress(-ValSeuil3);
                                    EditSeuil3.setText(Integer.toString(ValSeuil3));

                                } else {

                                    if (ValSeuil3 < ValSeuil4) {
                                        ValSeuil3 = -progress;
                                        SeekBar3.setProgress(-ValSeuil3);
                                        EditSeuil3.setText(Integer.toString(ValSeuil3));

                                        ValSeuil4 = -progress;
                                        SeekBar4.setProgress(-ValSeuil4);
                                        EditSeuil4.setText(Integer.toString(ValSeuil4));
                                    }

                                    if (ValSeuil3 > ValSeuil2) {
                                        if (ValSeuil2 < ValSeuil1) {
                                            ValSeuil3 = -progress;
                                            SeekBar3.setProgress(-ValSeuil3);
                                            EditSeuil3.setText(Integer.toString(ValSeuil4));

                                            ValSeuil2 = -progress;
                                            SeekBar2.setProgress(-ValSeuil3);
                                            EditSeuil2.setText(Integer.toString(ValSeuil2));
                                        } else {
                                            ValSeuil3 = -progress;
                                            SeekBar3.setProgress(-ValSeuil3);
                                            EditSeuil3.setText(Integer.toString(ValSeuil4));

                                            ValSeuil2 = -progress;
                                            SeekBar2.setProgress(-ValSeuil2);
                                            EditSeuil2.setText(Integer.toString(ValSeuil2));

                                            ValSeuil1 = -progress;
                                            SeekBar1.setProgress(-ValSeuil1);
                                            EditSeuil1.setText(Integer.toString(ValSeuil1));
                                        }
                                    }
                                }
                            }

                            @Override
                            public void onStartTrackingTouch(SeekBar seekBar) {

                            }

                            @Override
                            public void onStopTrackingTouch(SeekBar seekBar) {

                            }
                        };
                SeekBar.OnSeekBarChangeListener SeekBarListener4 =
                        new SeekBar.OnSeekBarChangeListener() {
                            @Override
                            public void onProgressChanged (SeekBar seekBar, int progress, boolean fromUser) {
                                final SeekBar SeekBar2;
                                final SeekBar SeekBar3;
                                final SeekBar SeekBar1;
                                final EditText EditSeuil1;
                                final EditText EditSeuil2;
                                final EditText EditSeuil3;
                                final EditText EditSeuil4;

                                //Seek Bar
                                SeekBar1 = (SeekBar) alertDialogViewModifSeuils.findViewById(R.id.seekBar1);
                                SeekBar2 = (SeekBar) alertDialogViewModifSeuils.findViewById(R.id.seekBar2);
                                SeekBar3 = (SeekBar) alertDialogViewModifSeuils.findViewById(R.id.seekBar3);

                                EditSeuil1 = (EditText) alertDialogViewModifSeuils.findViewById(R.id.EditSeuil1);
                                EditSeuil2 = (EditText) alertDialogViewModifSeuils.findViewById(R.id.EditSeuil2);
                                EditSeuil3 = (EditText) alertDialogViewModifSeuils.findViewById(R.id.EditSeuil3);
                                EditSeuil4 = (EditText) alertDialogViewModifSeuils.findViewById(R.id.EditSeuil4);

                                if(ValSeuil4<=ValSeuil3) {
                                    ValSeuil4 = -progress;
                                    EditSeuil4.setText(Integer.toString(ValSeuil4));
                                } else {
                                    if (ValSeuil3<=ValSeuil2){
                                        ValSeuil4 = -progress;
                                        EditSeuil4.setText(Integer.toString(ValSeuil4));

                                        ValSeuil3 = ValSeuil4;
                                        SeekBar3.setProgress(-ValSeuil3);
                                        EditSeuil3.setText(Integer.toString(ValSeuil4));
                                    } else {
                                        if (ValSeuil2 <= ValSeuil1) {
                                            ValSeuil4 = -progress;
                                            EditSeuil4.setText(Integer.toString(ValSeuil4));

                                            ValSeuil3 = -progress;
                                            SeekBar3.setProgress(-ValSeuil3);
                                            EditSeuil3.setText(Integer.toString(ValSeuil4));

                                            ValSeuil2 = -progress;
                                            SeekBar2.setProgress(-ValSeuil3);
                                            EditSeuil2.setText(Integer.toString(ValSeuil2));
                                        } else {
                                            ValSeuil4 = -progress;
                                            EditSeuil4.setText(Integer.toString(ValSeuil4));

                                            ValSeuil3 = -progress;
                                            SeekBar3.setProgress(-ValSeuil3);
                                            EditSeuil3.setText(Integer.toString(ValSeuil4));

                                            ValSeuil2 = -progress;
                                            SeekBar2.setProgress(-ValSeuil2);
                                            EditSeuil2.setText(Integer.toString(ValSeuil2));

                                            ValSeuil1 = -progress;
                                            SeekBar1.setProgress(-ValSeuil1);
                                            EditSeuil1.setText(Integer.toString(ValSeuil1));
                                        }
                                    }
                                }
                            }

                            @Override
                            public void onStartTrackingTouch(SeekBar seekBar) {

                            }

                            @Override
                            public void onStopTrackingTouch(SeekBar seekBar) {

                            }
                        };

                final EditText EditSeuil1;
                final EditText EditSeuil2;
                final EditText EditSeuil3;
                final EditText EditSeuil4;
                final SeekBar SeekBar1;
                final SeekBar SeekBar2;
                final SeekBar SeekBar3;
                final SeekBar SeekBar4;
                final TextView TextViewValSeuil1;
                final TextView TextViewValSeuil2;
                final TextView TextViewValSeuil3;
                final TextView TextViewValSeuil4;
                final TextView TextViewValSeuil1Texte;
                final TextView TextViewValSeuil2Texte;
                final TextView TextViewValSeuil3Texte;
                final TextView TextViewValSeuil4Texte;
                final TextView seuil1_texte;
                final TextView seuil2_texte;
                final TextView seuil3_texte;
                final TextView seuil4_texte;


                //Seek Bar
                SeekBar1 = (SeekBar) alertDialogViewModifSeuils.findViewById(R.id.seekBar1);
                SeekBar2 = (SeekBar) alertDialogViewModifSeuils.findViewById(R.id.seekBar2);
                SeekBar3 = (SeekBar) alertDialogViewModifSeuils.findViewById(R.id.seekBar3);
                SeekBar4 = (SeekBar) alertDialogViewModifSeuils.findViewById(R.id.seekBar4);
                SeekBar1.setOnSeekBarChangeListener(SeekBarListener1);
                SeekBar2.setOnSeekBarChangeListener(SeekBarListener2);
                SeekBar3.setOnSeekBarChangeListener(SeekBarListener3);
                SeekBar4.setOnSeekBarChangeListener(SeekBarListener4);

                // EditText
                EditSeuil1 = (EditText) alertDialogViewModifSeuils.findViewById(R.id.EditSeuil1);
                EditSeuil2 = (EditText) alertDialogViewModifSeuils.findViewById(R.id.EditSeuil2);
                EditSeuil3 = (EditText) alertDialogViewModifSeuils.findViewById(R.id.EditSeuil3);
                EditSeuil4 = (EditText) alertDialogViewModifSeuils.findViewById(R.id.EditSeuil4);

                // TextView
                seuil1_texte= (TextView) alertDialogViewModifSeuils.findViewById(R.id.seuil1_texte);
                seuil2_texte= (TextView) alertDialogViewModifSeuils.findViewById(R.id.seuil2_texte);
                seuil3_texte= (TextView) alertDialogViewModifSeuils.findViewById(R.id.seuil3_texte);
                seuil4_texte= (TextView) alertDialogViewModifSeuils.findViewById(R.id.seuil4_texte);
                TextViewValSeuil1 = (TextView) alertDialogViewModifSeuils.findViewById(R.id.Seuil1_auto_val);
                TextViewValSeuil2 = (TextView) alertDialogViewModifSeuils.findViewById(R.id.Seuil2_auto_val);
                TextViewValSeuil3 = (TextView) alertDialogViewModifSeuils.findViewById(R.id.Seuil3_auto_val);
                TextViewValSeuil4 = (TextView) alertDialogViewModifSeuils.findViewById(R.id.Seuil4_auto_val);
                TextViewValSeuil1Texte = (TextView) alertDialogViewModifSeuils.findViewById(R.id.Seuil1_auto);
                TextViewValSeuil2Texte = (TextView) alertDialogViewModifSeuils.findViewById(R.id.Seuil2_auto);
                TextViewValSeuil3Texte = (TextView) alertDialogViewModifSeuils.findViewById(R.id.Seuil3_auto);
                TextViewValSeuil4Texte = (TextView) alertDialogViewModifSeuils.findViewById(R.id.Seuil4_auto);



                // RadioGroup
                final RadioGroup radioGroup2=(RadioGroup) alertDialogViewModifSeuils.findViewById(R.id.radioGroup2);
                final RadioButton radio_default =(RadioButton) alertDialogViewModifSeuils.findViewById(R.id.radio_default);
                final RadioButton radio_indoor =(RadioButton) alertDialogViewModifSeuils.findViewById(R.id.radio_indoor);
                final RadioButton radio_perso =(RadioButton) alertDialogViewModifSeuils.findViewById(R.id.radio_perso);

                if(!modeIndoor && !modeOutdoor && !modePerso) {
                    SeekBar1.setVisibility(View.GONE);
                    SeekBar2.setVisibility(View.GONE);
                    SeekBar3.setVisibility(View.GONE);
                    SeekBar4.setVisibility(View.GONE);
                    EditSeuil1.setVisibility(View.GONE);
                    EditSeuil2.setVisibility(View.GONE);
                    EditSeuil3.setVisibility(View.GONE);
                    EditSeuil4.setVisibility(View.GONE);
                    seuil1_texte.setVisibility(View.GONE);
                    seuil2_texte.setVisibility(View.GONE);
                    seuil3_texte.setVisibility(View.GONE);
                    seuil4_texte.setVisibility(View.GONE);
                    TextViewValSeuil1.setVisibility(View.GONE);
                    TextViewValSeuil2.setVisibility(View.GONE);
                    TextViewValSeuil3.setVisibility(View.GONE);
                    TextViewValSeuil4.setVisibility(View.GONE);
                    TextViewValSeuil1Texte.setVisibility(View.GONE);
                    TextViewValSeuil2Texte.setVisibility(View.GONE);
                    TextViewValSeuil3Texte.setVisibility(View.GONE);
                    TextViewValSeuil4Texte.setVisibility(View.GONE);
                }
                if(modeOutdoor) {
                    SeekBar1.setVisibility(View.GONE);
                    SeekBar2.setVisibility(View.GONE);
                    SeekBar3.setVisibility(View.GONE);
                    SeekBar4.setVisibility(View.GONE);
                    EditSeuil1.setVisibility(View.GONE);
                    EditSeuil2.setVisibility(View.GONE);
                    EditSeuil3.setVisibility(View.GONE);
                    EditSeuil4.setVisibility(View.GONE);
                    seuil1_texte.setVisibility(View.GONE);
                    seuil2_texte.setVisibility(View.GONE);
                    seuil3_texte.setVisibility(View.GONE);
                    seuil4_texte.setVisibility(View.GONE);
                    TextViewValSeuil1.setText(Integer.toString(-70));
                    TextViewValSeuil2.setText(Integer.toString(-75));
                    TextViewValSeuil3.setText(Integer.toString(-80));
                    TextViewValSeuil4.setText(Integer.toString(-85));
                    radio_default.setChecked(true);
                }
                if(modeIndoor){
                    SeekBar1.setVisibility(View.GONE);
                    SeekBar2.setVisibility(View.GONE);
                    SeekBar3.setVisibility(View.GONE);
                    SeekBar4.setVisibility(View.GONE);
                    EditSeuil1.setVisibility(View.GONE);
                    EditSeuil2.setVisibility(View.GONE);
                    EditSeuil3.setVisibility(View.GONE);
                    EditSeuil4.setVisibility(View.GONE);
                    seuil1_texte.setVisibility(View.GONE);
                    seuil2_texte.setVisibility(View.GONE);
                    seuil3_texte.setVisibility(View.GONE);
                    seuil4_texte.setVisibility(View.GONE);
                    TextViewValSeuil1.setVisibility(View.VISIBLE);
                    TextViewValSeuil2.setVisibility(View.VISIBLE);
                    TextViewValSeuil3.setVisibility(View.VISIBLE);
                    TextViewValSeuil4.setVisibility(View.VISIBLE);
                    TextViewValSeuil1Texte.setVisibility(View.VISIBLE);
                    TextViewValSeuil2Texte.setVisibility(View.VISIBLE);
                    TextViewValSeuil3Texte.setVisibility(View.VISIBLE);
                    TextViewValSeuil4Texte.setVisibility(View.VISIBLE);
                    TextViewValSeuil1.setText(Integer.toString(-63));
                    TextViewValSeuil2.setText(Integer.toString(-68));
                    TextViewValSeuil3.setText(Integer.toString(-73));
                    TextViewValSeuil4.setText(Integer.toString(-78));
                    radio_indoor.setChecked(true);
                }
                if(modePerso){
                    /// Rajouter un boolean pour enregistrer les valeurs à la fin !

                    SeekBar1.setVisibility(View.VISIBLE);
                    SeekBar2.setVisibility(View.VISIBLE);
                    SeekBar3.setVisibility(View.VISIBLE);
                    SeekBar4.setVisibility(View.VISIBLE);
                    EditSeuil1.setVisibility(View.VISIBLE);
                    EditSeuil2.setVisibility(View.VISIBLE);
                    EditSeuil3.setVisibility(View.VISIBLE);
                    EditSeuil4.setVisibility(View.VISIBLE);
                    seuil1_texte.setVisibility(View.VISIBLE);
                    seuil2_texte.setVisibility(View.VISIBLE);
                    seuil3_texte.setVisibility(View.VISIBLE);
                    seuil4_texte.setVisibility(View.VISIBLE);
                    TextViewValSeuil1.setVisibility(View.GONE);
                    TextViewValSeuil2.setVisibility(View.GONE);
                    TextViewValSeuil3.setVisibility(View.GONE);
                    TextViewValSeuil4.setVisibility(View.GONE);
                    TextViewValSeuil1Texte.setVisibility(View.GONE);
                    TextViewValSeuil2Texte.setVisibility(View.GONE);
                    TextViewValSeuil3Texte.setVisibility(View.GONE);
                    TextViewValSeuil4Texte.setVisibility(View.GONE);
                    radio_perso.setChecked(true);
                }

                radioGroup2.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                        switch(checkedId) {
                            case R.id.radio_default :
                                SeekBar1.setVisibility(View.GONE);
                                SeekBar2.setVisibility(View.GONE);
                                SeekBar3.setVisibility(View.GONE);
                                SeekBar4.setVisibility(View.GONE);
                                EditSeuil1.setVisibility(View.GONE);
                                EditSeuil2.setVisibility(View.GONE);
                                EditSeuil3.setVisibility(View.GONE);
                                EditSeuil4.setVisibility(View.GONE);
                                seuil1_texte.setVisibility(View.GONE);
                                seuil2_texte.setVisibility(View.GONE);
                                seuil3_texte.setVisibility(View.GONE);
                                seuil4_texte.setVisibility(View.GONE);
                                TextViewValSeuil1.setVisibility(View.VISIBLE);
                                TextViewValSeuil2.setVisibility(View.VISIBLE);
                                TextViewValSeuil3.setVisibility(View.VISIBLE);
                                TextViewValSeuil4.setVisibility(View.VISIBLE);
                                TextViewValSeuil1Texte.setVisibility(View.VISIBLE);
                                TextViewValSeuil2Texte.setVisibility(View.VISIBLE);
                                TextViewValSeuil3Texte.setVisibility(View.VISIBLE);
                                TextViewValSeuil4Texte.setVisibility(View.VISIBLE);
                                TextViewValSeuil1.setText(Integer.toString(-70));
                                TextViewValSeuil2.setText(Integer.toString(-75));
                                TextViewValSeuil3.setText(Integer.toString(-80));
                                TextViewValSeuil4.setText(Integer.toString(-85));
                                Seuil1 = -70;
                                Seuil2 = -75;
                                Seuil3 = -80;
                                Seuil4 = -85;
                                modeOutdoor=true;
                                modeIndoor=false;
                                modePerso=false;
                                break;
                            case R.id.radio_indoor :
                                SeekBar1.setVisibility(View.GONE);
                                SeekBar2.setVisibility(View.GONE);
                                SeekBar3.setVisibility(View.GONE);
                                SeekBar4.setVisibility(View.GONE);
                                EditSeuil1.setVisibility(View.GONE);
                                EditSeuil2.setVisibility(View.GONE);
                                EditSeuil3.setVisibility(View.GONE);
                                EditSeuil4.setVisibility(View.GONE);
                                seuil1_texte.setVisibility(View.GONE);
                                seuil2_texte.setVisibility(View.GONE);
                                seuil3_texte.setVisibility(View.GONE);
                                seuil4_texte.setVisibility(View.GONE);
                                TextViewValSeuil1.setVisibility(View.VISIBLE);
                                TextViewValSeuil2.setVisibility(View.VISIBLE);
                                TextViewValSeuil3.setVisibility(View.VISIBLE);
                                TextViewValSeuil4.setVisibility(View.VISIBLE);
                                TextViewValSeuil1Texte.setVisibility(View.VISIBLE);
                                TextViewValSeuil2Texte.setVisibility(View.VISIBLE);
                                TextViewValSeuil3Texte.setVisibility(View.VISIBLE);
                                TextViewValSeuil4Texte.setVisibility(View.VISIBLE);
                                TextViewValSeuil1.setText(Integer.toString(-63));
                                TextViewValSeuil2.setText(Integer.toString(-68));
                                TextViewValSeuil3.setText(Integer.toString(-73));
                                TextViewValSeuil4.setText(Integer.toString(-78));
                                Seuil1 = -63;
                                Seuil2 = -68;
                                Seuil3 = -73;
                                Seuil4 = -78;
                                modeIndoor=true;
                                modePerso=false;
                                modeOutdoor=false;
                                break;
                            case R.id.radio_perso :
                                ValSeuil1=Seuil1;
                                ValSeuil2=Seuil2;
                                ValSeuil3=Seuil3;
                                ValSeuil4=Seuil4;
                                SeekBar1.setVisibility(View.VISIBLE);
                                SeekBar2.setVisibility(View.VISIBLE);
                                SeekBar3.setVisibility(View.VISIBLE);
                                SeekBar4.setVisibility(View.VISIBLE);
                                EditSeuil1.setVisibility(View.VISIBLE);
                                EditSeuil2.setVisibility(View.VISIBLE);
                                EditSeuil3.setVisibility(View.VISIBLE);
                                EditSeuil4.setVisibility(View.VISIBLE);
                                seuil1_texte.setVisibility(View.VISIBLE);
                                seuil2_texte.setVisibility(View.VISIBLE);
                                seuil3_texte.setVisibility(View.VISIBLE);
                                seuil4_texte.setVisibility(View.VISIBLE);
                                TextViewValSeuil1.setVisibility(View.GONE);
                                TextViewValSeuil2.setVisibility(View.GONE);
                                TextViewValSeuil3.setVisibility(View.GONE);
                                TextViewValSeuil4.setVisibility(View.GONE);
                                TextViewValSeuil1Texte.setVisibility(View.GONE);
                                TextViewValSeuil2Texte.setVisibility(View.GONE);
                                TextViewValSeuil3Texte.setVisibility(View.GONE);
                                TextViewValSeuil4Texte.setVisibility(View.GONE);
                                modePerso=true;
                                modeIndoor=false;
                                modeOutdoor=false;
                                break;
                            default:
                                break;
                        }
                    }

                });

                // Enregistrement de la configuration
                alertdial.setPositiveButton("Modifier", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if(radio_perso.isChecked()) {
                            if(EditSeuil1.getText().toString().length()!=0) {
                                Seuil1 = Integer.parseInt(EditSeuil1.getText().toString());
                            } else {
                                Seuil1=-70;
                            }
                            if(EditSeuil2.getText().toString().length()!=0) {
                                Seuil2 = Integer.parseInt(EditSeuil2.getText().toString());
                            } else{
                                Seuil2=-75;
                            }
                            if(EditSeuil3.getText().toString().length()!=0) {
                                Seuil3 = Integer.parseInt(EditSeuil3.getText().toString());
                            } else {
                                Seuil3=-80;
                            }
                            if(EditSeuil4.getText().toString().length()!=0) {
                                Seuil4 = Integer.parseInt(EditSeuil4.getText().toString());
                            }else{
                                Seuil4=-85;
                            }
                        }

                        updateSeuils();
                    }
                });
                alertdial.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // On ne fais rien si l'utilisateur clique sur annuler
                    }
                });
                alertdial.setIcon(android.R.drawable.ic_dialog_info);
                alertdial.show();
                onPause();
            }
        });



        modifMAC.setOnClickListener(new View.OnClickListener() {
            public void onClick(final View View) {
                lectureFichier();
            }
        });

        modifGranularite.setOnClickListener(new View.OnClickListener() {
            public void onClick(final View View) {
                final View alertDialogViewModifGranularite = alertdialogModifGranularite.inflate(R.layout.alertdialog_modif_granularite, null);
                final AlertDialog.Builder alertdial = new AlertDialog.Builder(Carte.this);
                alertdial.setView(alertDialogViewModifGranularite);
                alertdial.setTitle("Selection des émetteurs WIFI");


                final SeekBar.OnSeekBarChangeListener SeekBarListener5 =
                        new SeekBar.OnSeekBarChangeListener() {
                            @Override
                            public void onProgressChanged (SeekBar seekBar, int progress, boolean fromUser) {
                                ValSeuil5=progress;
                                final EditText EditSeuil5 = (EditText) alertDialogViewModifGranularite.findViewById(R.id.EditSeuil5);
                                EditSeuil5.setText(Integer.toString(ValSeuil5));
                            }

                            @Override
                            public void onStartTrackingTouch(SeekBar seekBar) {

                            }

                            @Override
                            public void onStopTrackingTouch(SeekBar seekBar) {

                            }
                        };

                SeekBar SeekBar5 = (SeekBar) alertDialogViewModifGranularite.findViewById(R.id.seekBar5);
                SeekBar5.setOnSeekBarChangeListener(SeekBarListener5);

                final EditText EditSeuil5 = (EditText) alertDialogViewModifGranularite.findViewById(R.id.EditSeuil5);
                EditSeuil5.setText("10");
                SeekBar5.setProgress(10);

                // Enregistrement de la configuration
                alertdial.setPositiveButton("Modifier", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if(Integer.parseInt(EditSeuil5.getText().toString())==0){
                            Seuil5=1;
                        } else {
                            Seuil5 = Integer.parseInt(EditSeuil5.getText().toString());
                        }

                        File fichiersTemp = new File(Environment.getExternalStorageDirectory() + File.separator + "WifiStateData" + File.separator + "FichiersTemp");
                        File[] listFiles = fichiersTemp.listFiles();

                        if (fichiersTemp.exists()) {
                            for (int i = 0; i < listFiles.length; i++) {
                                listFiles[i].delete();
                            }
                            traitementMesures();
                        }
                    }
                });
                alertdial.setIcon(android.R.drawable.ic_dialog_info);
                alertdial.show();
                onPause();
            }
        });

        switchCarte.setOnClickListener(new View.OnClickListener() {
            public void onClick(View View) {
                modifOption=false;
                if(radio_level_bool){
                    switchCanalActif=false;
                    switchMACActif=false;
                    traitementRapide2();
                }
                if(radio_canaux_bool){
                    switchCanalActif=true;
                    switchMACActif=false;
                    if(nbPrediction!=0){
                        nbPrediction=0;
                        Prediction.razCasesParcouruesSansPrediction();
                    }
                    traitementRapide();
                }
                if(radio_mac_bool){
                    switchCanalActif=false;
                    switchMACActif=true;
                    if(nbPrediction!=0){
                        nbPrediction=0;

                        Prediction.razCasesParcouruesSansPrediction();

                    }
                    traitementRapide();
                }

            }
        });

        predireButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View predireButtonView) {

                nbPrediction++;

                Log.d("nbPrediction",Integer.toString(nbPrediction));
                if(nbPrediction<2) {
                    casesParcouruesSansPrediction = new boolean [(int) Traitement.getNombreCase()];
                    for(int j=0;j<Traitement.getNombreCase();j++){
                        casesParcouruesSansPrediction[j]=Traitement.getCasesParcourues(j);
                        Log.d("caseParcourSansPre1",Boolean.toString(casesParcouruesSansPrediction[j]));
                    }

                    nbCasesPredites=0;
                    nbCasesParcouruesInit=0;

                    for(int i=0; i<Traitement.getNombreCase();i++){
                        if(Traitement.getCasesParcourues(i)){
                            nbCasesParcouruesInit++;
                        }
                    }



                    prediction();
                } else {
                    predictionInfini();
                }
            }
        });

        legendeButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                geneLegende();
                //Parametres pour gérer la largeur et la hauteur de la légende en pourcentage de la taille de l'écran
            }
        });
    }

    private void updateSeuils(){
        mProgressDialog = ProgressDialog.show(this, "Patientez...",
                "Long operation starts...", true);

        // useful code, variables declarations...
        new Thread((new Runnable() {
            @Override
            public void run() {
                Message msg = null;
                String progressBarData = "Mise à jour des seuils...";

                // populates the message
                msg = handlerUpdateSeuils.obtainMessage(MSG_IND, (Object) progressBarData);

                // sends the message to our handler
                handlerUpdateSeuils.sendMessage(msg);

                // starts the first operation
                status=Traitement.updateSeuils();

                if (Accueil.ErrorStatus.NO_ERROR != status) {
                    Log.e("erreur2_tM", "error while computing the path status:"
                            + status);
                    // error management,creates an error message
                    msg = handlerUpdateSeuils.obtainMessage(MSG_ERR,
                            "error while computing the path status:"
                                    + status);
                    // sends the message to our handler
                    handlerUpdateSeuils.sendMessage(msg);
                } else {
                    //progressBarData="Succès";
                    msg = handlerUpdateSeuils.obtainMessage(MSG_CNF,
                            (Object) progressBarData);
                    // sends the message to our handler
                    handlerUpdateSeuils.sendMessage(msg);
                }
            }
        })).start();
    }

    final Handler handlerUpdateSeuils = new Handler() {
        public void handleMessage(Message msg) {
            String text2display = null;
            switch (msg.what) {
                case MSG_IND:
                    if (mProgressDialog.isShowing()) {
                        mProgressDialog.setMessage(((String) msg.obj));
                    }
                    break;
                case MSG_ERR:
                    text2display = (String) msg.obj;
                    Toast.makeText(getApplicationContext(), "Error: " + text2display,
                            Toast.LENGTH_LONG).show();
                    if (mProgressDialog.isShowing()) {
                        mProgressDialog.dismiss();
                    }
                    break;
                case MSG_CNF:
                    if (mProgressDialog.isShowing()) {
                        mProgressDialog.dismiss();
                    }

                    Intent intent = new Intent(Carte.this, Carte.class);
                    // Extraction des seuils vers l'activité carte
                    intent.putExtra(EXTRA_SEUIL1, Seuil1);
                    intent.putExtra(EXTRA_SEUIL2, Seuil2);
                    intent.putExtra(EXTRA_SEUIL3, Seuil3);
                    intent.putExtra(EXTRA_SEUIL4, Seuil4);
                    startActivity(intent);
                    break;
                default: // should never happen
                    break;
            }
        }
    };

    private void generationPolygon(GoogleMap map){
        int cpt =0;
        double latSud;
        double latNord;
        double longOuest;
        double longEst;
        for(int j=0;j<Traitement.getNombreCaseLat();j++) {
            // Récupération d'une partie des coordonnées d'un carré Latitude sud et latitude nord
            latSud = Double.parseDouble(Traitement.wordAtTemp1(1,j));
            latNord = Double.parseDouble(Traitement.wordAtTemp1(1,j+1));
            if(switchMACActif == false && switchCanalActif == false) {
                for (int i = 0; i < Traitement.getNombreCaseLong(); i++) { // On démarre à la deuxième, la première case suivant la longitude est declaré juste avant
                    // Récupération du reste des coordonnées d'un carré longitude ouest et longitude Est
                    longOuest = Double.parseDouble(Traitement.wordAtTemp1(0,i));
                    longEst = Double.parseDouble(Traitement.wordAtTemp1(0,i+1));
                    // Coloration des carrés
                    switch (Traitement.getTabCouleur(cpt)) {
                        case 0:
                            Polygon polygon_p = map.addPolygon(new PolygonOptions()
                                    .add(new LatLng(latSud, longOuest), new LatLng(latNord, longOuest), new LatLng(latNord, longEst), new LatLng(latSud, longEst)) // SO, NO, NE, SE
                                    .strokeColor(Color.BLACK) //Couleur du contour
                                    .fillColor(0x8F009900));//Couleur de remplissage, possibilité d'associer une couleur à un niveau en dBm
                            polygon_p.setStrokeWidth(0);  //Définition de l'épaisseur de la ligne
                            break;
                        case 1:
                            Polygon polygon_p1 = map.addPolygon(new PolygonOptions()
                                    .add(new LatLng(latSud, longOuest), new LatLng(latNord, longOuest), new LatLng(latNord, longEst), new LatLng(latSud, longEst)) // SO, NO, NE, SE
                                    .strokeColor(Color.BLACK) //Couleur du contour
                                    .fillColor(0x8FCCCC33));//Couleur de remplissage, possibilité d'associer une couleur à un niveau en dBm
                            polygon_p1.setStrokeWidth(0);  //Définition de l'épaisseur de la ligne
                            break;
                        case 2:
                            Polygon polygon_p2 = map.addPolygon(new PolygonOptions()
                                    .add(new LatLng(latSud, longOuest), new LatLng(latNord, longOuest), new LatLng(latNord, longEst), new LatLng(latSud, longEst)) // SO, NO, NE, SE // SO, NO, NE, SE
                                    .strokeColor(Color.BLACK) //Couleur du contour
                                    .fillColor(0x8FCC6600));//Couleur de remplissage, possibilité d'associer une couleur à un niveau en dBm
                            polygon_p2.setStrokeWidth(0);  //Définition de l'épaisseur de la ligne
                            break;
                        case 3:
                            Polygon polygon_p3 = map.addPolygon(new PolygonOptions()
                                    .add(new LatLng(latSud, longOuest), new LatLng(latNord, longOuest), new LatLng(latNord, longEst), new LatLng(latSud, longEst))  // SO, NO, NE, SE
                                    .strokeColor(Color.BLACK) //Couleur du contour
                                    .fillColor(0x8FCC0000));//Couleur de remplissage, possibilité d'associer une couleur à un niveau en dBm
                            polygon_p3.setStrokeWidth(0);  //Définition de l'épaisseur de la ligne
                            break;
                        case 4:
                            Polygon polygon_p4 = map.addPolygon(new PolygonOptions()
                                    .add(new LatLng(latSud, longOuest), new LatLng(latNord, longOuest), new LatLng(latNord, longEst), new LatLng(latSud, longEst))  // SO, NO, NE, SE
                                    .strokeColor(Color.BLACK) //Couleur du contour
                                    .fillColor(0x8FAFAFAF));//Couleur de remplissage, possibilité d'associer une couleur à un niveau en dBm

                            polygon_p4.setStrokeWidth(0);  //Définition de l'épaisseur de la ligne
                            break;
                        case 5:
                            Polygon polygon_p5 = map.addPolygon(new PolygonOptions()
                                    .add(new LatLng(latSud, longOuest), new LatLng(latNord, longOuest), new LatLng(latNord, longEst), new LatLng(latSud, longEst))  // SO, NO, NE, SE
                                    .strokeColor(Color.TRANSPARENT) //Couleur du contour
                                    .fillColor(Color.TRANSPARENT));//Couleur de remplissage, possibilité d'associer une couleur à un niveau en dBm
                            polygon_p5.setStrokeWidth(0);  //Définition de l'épaisseur de la ligne
                            break;
                    }
                    cpt++;
                }
            }

            if(switchCanalActif == true){
                for (int i = 0; i < Traitement.getNombreCaseLong(); i++) {
                    longOuest = Double.parseDouble(Traitement.wordAtTemp1(0,i));
                    longEst = Double.parseDouble(Traitement.wordAtTemp1(0,i+1));
                    if(Traitement.getCasesParcourues(cpt)) {
                        if(Traitement.getTabLevelMoyCanaux(cpt)!=SeuilPasDeValeur) {
                            Log.d("Case_Avec_Réseau", Integer.toString(cpt));
                            Polygon polygon_p6 = map.addPolygon(new PolygonOptions()
                                    .add(new LatLng(latSud, longOuest), new LatLng(latNord, longOuest), new LatLng(latNord, longEst), new LatLng(latSud, longEst))  // SO, NO, NE, SE
                                    .strokeColor(Color.BLACK) //Couleur du contour
                                    .fillColor(Color.parseColor("#" + couleurAleatoire[indiceCanalPredominant[cpt]])));//Couleur de remplissage, possibilité d'associer une couleur à un niveau en dBm
                            polygon_p6.setStrokeWidth(0);  //Définition de l'épaisseur de la ligne
                            cpt++;
                        } else {
                            Log.d("Case_Hors_réseau_Canaux", Integer.toString(cpt));
                            Polygon polygon_p7 = map.addPolygon(new PolygonOptions()
                                    .add(new LatLng(latSud, longOuest), new LatLng(latNord, longOuest), new LatLng(latNord, longEst), new LatLng(latSud, longEst))  // SO, NO, NE, SE
                                    .strokeColor(Color.BLACK) //Couleur du contour
                                    .fillColor(0x8FAFAFAF));//Couleur de remplissage, possibilité d'associer une couleur à un niveau en dBm

                            polygon_p7.setStrokeWidth(0);  //Définition de l'épaisseur de la ligne
                            cpt++;
                        }
                    } else {
                        Log.d("CaseNonParcourue2", Integer.toString(cpt));
                        Polygon polygon_p8 = map.addPolygon(new PolygonOptions()
                                .add(new LatLng(latSud, longOuest), new LatLng(latNord, longOuest), new LatLng(latNord, longEst), new LatLng(latSud, longEst))  // SO, NO, NE, SE
                                .strokeColor(Color.TRANSPARENT) //Couleur du contour
                                //.fillColor(Integer.decode(couleurAleatoire[indiceMacPredominant[cpt]])));//Couleur de remplissage, possibilité d'associer une couleur à un niveau en dBm
                                .fillColor(Color.TRANSPARENT));//Couleur de remplissage, possibilité d'associer une couleur à un niveau en dBm
                        polygon_p8.setStrokeWidth(0);  //Définition de l'épaisseur de la ligne
                        cpt++;
                    }
                }
            }

            if(switchMACActif == true){
                for (int i = 0; i < Traitement.getNombreCaseLong(); i++) {
                    longOuest = Double.parseDouble(Traitement.wordAtTemp1(0,i));
                    longEst = Double.parseDouble(Traitement.wordAtTemp1(0,i+1));
                    if(Traitement.getCasesParcourues(cpt)) {
                        if(Traitement.getTabLevelMoy(cpt)!=SeuilPasDeValeur) {
                            Log.d("Case_Avec_Réseau", Integer.toString(cpt));
                            Polygon polygon_p9 = map.addPolygon(new PolygonOptions()
                                    .add(new LatLng(latSud, longOuest), new LatLng(latNord, longOuest), new LatLng(latNord, longEst), new LatLng(latSud, longEst))  // SO, NO, NE, SE
                                    .strokeColor(Color.BLACK) //Couleur du contour
                                    .fillColor(Color.parseColor("#" + couleurAleatoire[indiceMacPredominant[cpt]])));//Couleur de remplissage, possibilité d'associer une couleur à un niveau en dBm

                            polygon_p9.setStrokeWidth(0);  //Définition de l'épaisseur de la ligne

                            cpt++;
                        } else {
                            Log.d("Case_hors_réseau_Mac", Integer.toString(cpt));
                            Polygon polygon_p10 = map.addPolygon(new PolygonOptions()
                                    .add(new LatLng(latSud, longOuest), new LatLng(latNord, longOuest), new LatLng(latNord, longEst), new LatLng(latSud, longEst))  // SO, NO, NE, SE
                                    .strokeColor(Color.BLACK) //Couleur du contour
                                    .fillColor(0x8FAFAFAF));//Couleur de remplissage, possibilité d'associer une couleur à un niveau en dBm
                            polygon_p10.setStrokeWidth(0);  //Définition de l'épaisseur de la ligne
                            cpt++;
                        }
                    } else {
                        Log.d("CaseNonParcourue2", Integer.toString(cpt));
                        Polygon polygon_p11 = map.addPolygon(new PolygonOptions()
                                .add(new LatLng(latSud, longOuest), new LatLng(latNord, longOuest), new LatLng(latNord, longEst), new LatLng(latSud, longEst))  // SO, NO, NE, SE
                                .strokeColor(Color.TRANSPARENT) //Couleur du contour
                                //.fillColor(Integer.decode(couleurAleatoire[indiceMacPredominant[cpt]])));//Couleur de remplissage, possibilité d'associer une couleur à un niveau en dBm
                                .fillColor(Color.TRANSPARENT));//Couleur de remplissage, possibilité d'associer une couleur à un niveau en dBm
                        polygon_p11.setStrokeWidth(0);  //Définition de l'épaisseur de la ligne
                        cpt++;
                    }
                }
            }
        }
    }

    private void prediction(){
        mProgressDialog = ProgressDialog.show(this, "Patientez...",
                "Long operation starts...", true);

        // useful code, variables declarations...
        new Thread((new Runnable() {
            @Override
            public void run() {
                Message msg = null;
                String progressBarData = "Prediction de case...";

                // populates the message
                msg = handlerPrediction.obtainMessage(MSG_IND, (Object) progressBarData);

                // sends the message to our handler
                handlerPrediction.sendMessage(msg);

                // starts the first long operation
                status=Prediction.determinationZonesVoisines(); //// fonction longue
                //status =Traitement.lectureDataTemp1();


                if (Accueil.ErrorStatus.NO_ERROR != status) {
                    Log.e("erreur2_tM", "error while computing the path status:"
                            + status);
                    // error management,creates an error message
                    msg = handlerPrediction.obtainMessage(MSG_ERR,
                            "error while computing the path status:"
                                    + status);
                    // sends the message to our handler
                    handlerPrediction.sendMessage(msg);
                } else {
                    //progressBarData="Succès";
                    msg = handlerPrediction.obtainMessage(MSG_CNF,
                            (Object) progressBarData);
                    // sends the message to our handler
                    handlerPrediction.sendMessage(msg);
                }
            }
        })).start();
    }

    final Handler handlerPrediction = new Handler() {
        public void handleMessage(Message msg) {
            String text2display = null;
            switch (msg.what) {
                case MSG_IND:
                    if (mProgressDialog.isShowing()) {
                        mProgressDialog.setMessage(((String) msg.obj));
                    }
                    break;
                case MSG_ERR:
                    text2display = (String) msg.obj;
                    Toast.makeText(getApplicationContext(), "Error: " + text2display,
                            Toast.LENGTH_LONG).show();
                    if (mProgressDialog.isShowing()) {
                        mProgressDialog.dismiss();
                    }
                    break;
                case MSG_CNF:
                    if (mProgressDialog.isShowing()) {
                        mProgressDialog.dismiss();
                    }

                    Log.d("prediboolHandler",Boolean.toString(predibool1));
                    predibool1 = true;
                    Log.d("prediboolHandler2",Boolean.toString(predibool1));

                    Intent refreshCarte = new Intent(Carte.this, Carte.class);
                    startActivity(refreshCarte);
                    break;
                default: // should never happen
                    break;
            }
        }
    };

    private void predictionInfini(){
        mProgressDialog = ProgressDialog.show(this, "Patientez...",
                "Long operation starts...", true);

        // useful code, variables declarations...
        new Thread((new Runnable() {
            @Override
            public void run() {
                Message msg = null;
                String progressBarData = "Prediction de case...";

                // populates the message
                msg = handerPredictionInfini.obtainMessage(MSG_IND, (Object) progressBarData);

                // sends the message to our handler
                handerPredictionInfini.sendMessage(msg);

                // starts the first long operation
                status=Prediction.determinationZonesVoisinesInfinis(); //// fonction longue
                //status =Traitement.lectureDataTemp1();


                if (Accueil.ErrorStatus.NO_ERROR != status) {
                    Log.e("erreur2_tM", "error while computing the path status:"
                            + status);
                    // error management,creates an error message
                    msg = handerPredictionInfini.obtainMessage(MSG_ERR,
                            "error while computing the path status:"
                                    + status);
                    // sends the message to our handler
                    handerPredictionInfini.sendMessage(msg);
                } else {
                    //progressBarData="Succès";
                    msg = handerPredictionInfini.obtainMessage(MSG_CNF,
                            (Object) progressBarData);
                    // sends the message to our handler
                    handerPredictionInfini.sendMessage(msg);
                }
            }
        })).start();
    }

    final Handler handerPredictionInfini = new Handler() {
        public void handleMessage(Message msg) {
            String text2display = null;
            switch (msg.what) {
                case MSG_IND:
                    if (mProgressDialog.isShowing()) {
                        mProgressDialog.setMessage(((String) msg.obj));
                    }
                    break;
                case MSG_ERR:
                    text2display = (String) msg.obj;
                    Toast.makeText(getApplicationContext(), "Error: " + text2display,
                            Toast.LENGTH_LONG).show();
                    if (mProgressDialog.isShowing()) {
                        mProgressDialog.dismiss();
                    }
                    break;
                case MSG_CNF:
                    if (mProgressDialog.isShowing()) {
                        mProgressDialog.dismiss();
                    }

                    predibool2 = true;

                    // Refresh de l'activité carte suite à la prediction
                    Intent refreshCarte = new Intent(Carte.this, Carte.class);
                    startActivity(refreshCarte);
                    break;
                default: // should never happen
                    break;
            }
        }
    };

    private void geneLegende(){
        final LinearLayout lila1= new LinearLayout(this);
        lila1.setOrientation(LinearLayout.VERTICAL);     //Paramètre pour que la liste soit verticale
        ScrollView scrollContainer = new ScrollView(this);
        scrollContainer.addView (lila1);
        scrollContainer.setVerticalScrollBarEnabled(true);
        scrollContainer.setScrollBarStyle(View.SCROLLBARS_INSIDE_INSET);
        scrollContainer.scrollTo(couleurAleatoire.length, 0);

        for (int i = 0; i < couleurAleatoire.length; i++){
            txt0 = new TextView(this);
            if(txt0.getParent()!=null)
                ((ViewGroup)txt0.getParent()).removeView(txt0);
            txt1 = new TextView(this);
            if(txt1.getParent()!=null)
                ((ViewGroup)txt1.getParent()).removeView(txt1);
            if(!choixMacMAJBoolean) {
                if (switchMACActif) {
                    Log.d("setTextLegend", choixMac.get(i));
                    txt0.setText("\n\t" + choixMac.get(i));
                }
                if (switchCanalActif) {
                    Log.d("setTextLegend", choixCanal.get(i));
                    txt0.setText("\n\t" + choixCanal.get(i));
                }

                lila1.addView(txt0);           //On ajoute au lila chaque View de ListLegende, le tableau de TextView créé grâce à CouleurAléatoire
                txt1.setText("\t■■■■■■");
                txt1.setTextColor(Color.parseColor("#" + couleurAleatoire[i]));   //On affecte à txt1 la couleur aléatoire de l'émetteur
                lila1.addView(txt1);
            } else {
                if (switchMACActif) {
                    boolean reseauExterneBool=false;
                    for(int j=0;j<choixMacReseauExterne.size();j++) {
                        if ((choixMac.get(i)).equals((choixMacReseauExterne).get(j))) {
                            reseauExterneBool=true;
                        }
                    }
                    if (reseauExterneBool) {
                        Log.d("setTextLegend", choixMac.get(i));
                        txt0.setText("\n\t" + choixMac.get(i) + " (Autre)");
                    } else {
                        Log.d("setTextLegend", choixMac.get(i));
                        txt0.setText("\n\t" + choixMac.get(i)+ " ("+choixSSID+")");
                    }
                }
                if (switchCanalActif) {
                    Log.d("setTextLegend", choixCanal.get(i));
                    txt0.setText("\n\t" + choixCanal.get(i));
                }

                lila1.addView(txt0);           //On ajoute au lila chaque View de ListLegende, le tableau de TextView créé grâce à CouleurAléatoire
                txt1.setText("\t■■■■■■");
                txt1.setTextColor(Color.parseColor("#" + couleurAleatoire[i]));   //On affecte à txt1 la couleur aléatoire de l'émetteur
                lila1.addView(txt1);
            }
        }
        final int width = (int)(getResources().getDisplayMetrics().widthPixels*0.30);
        final int height = (int)(getResources().getDisplayMetrics().heightPixels*0.79);

        //Constructeur de l'alertdialog
        legendAlertDialog = new AlertDialog.Builder(Carte.this);
        legendAlertDialog.setTitle("Légende");
        legendAlertDialog.setIcon(android.R.drawable.ic_menu_mapmode);
        legendAlertDialog.setView(lila1);
        legendAlertDialog.setView(scrollContainer);
        final AlertDialog dlg = legendAlertDialog.show();

        dlg.getWindow().setLayout(width, height);           //on affecte les paramètres des lignes 49-50 à la fenetre de de légende
        dlg.getWindow().getAttributes().dimAmount = 0.0F;   //Contraste de l'arrière plan 0.FF -> noir | 0.0F -> clair
        dlg.getWindow().setGravity(LEFT | BOTTOM);//On fixe la légende à gauche
    }

    public static int getNbCasesParcouruesInit (){return nbCasesParcouruesInit;}

    private void traitementRapide(){
        mProgressDialog = ProgressDialog.show(this, "Patientez...",
                "Long operation starts...", true);

        // useful code, variables declarations...
        new Thread((new Runnable() {
            @Override
            public void run() {
                Message msg = null;
                String progressBarData = "Génération de la nouvelle carte...";

                // populates the message
                msg = handlerTraitementRapide.obtainMessage(MSG_IND, (Object) progressBarData);

                // sends the message to our handler
                handlerTraitementRapide.sendMessage(msg);

                // starts the first long operation
                status = Traitement.colorCases2();

                if (Accueil.ErrorStatus.NO_ERROR != status) {
                    Log.e("erreur2_tM", "error while computing the path status:"
                            + status);
                    // error management,creates an error message
                    msg = handlerTraitementRapide.obtainMessage(MSG_ERR,
                            "error while computing the path status:"
                                    + status);
                    // sends the message to our handler
                    handlerTraitementRapide.sendMessage(msg);
                } else {
                    //progressBarData="Succès";
                    msg = handlerTraitementRapide.obtainMessage(MSG_CNF,
                            (Object) progressBarData);
                    // sends the message to our handler
                    handlerTraitementRapide.sendMessage(msg);
                }
            }
        })).start();
    }
    final Handler handlerTraitementRapide = new Handler() {
        public void handleMessage(Message msg) {
            String text2display = null;
            switch (msg.what) {
                case MSG_IND:
                    if (mProgressDialog.isShowing()) {
                        mProgressDialog.setMessage(((String) msg.obj));
                    }
                    break;
                case MSG_ERR:
                    text2display = (String) msg.obj;
                    Toast.makeText(getApplicationContext(), "Error: " + text2display,
                            Toast.LENGTH_LONG).show();
                    if (mProgressDialog.isShowing()) {
                        mProgressDialog.dismiss();
                    }
                    break;
                case MSG_CNF:
                    if (mProgressDialog.isShowing()) {
                        mProgressDialog.dismiss();
                    }
                    // CARTE
                    nbPrediction = 0;
                    Intent intent = new Intent(Carte.this, Carte.class);
                    Log.d("Seuil4", Integer.toString(Seuil4));
                    // Extraction des seuils vers l'activité carte
                    intent.putExtra(EXTRA_SEUIL1, Seuil1);
                    intent.putExtra(EXTRA_SEUIL2, Seuil2);
                    intent.putExtra(EXTRA_SEUIL3, Seuil3);
                    intent.putExtra(EXTRA_SEUIL4, Seuil4);
                    startActivity(intent);
                    break;
                default: // should never happen
                    break;
            }
        }
    };

    private void traitementRapide2(){
        mProgressDialog = ProgressDialog.show(this, "Patientez...",
                "Long operation starts...", true);

        // useful code, variables declarations...
        new Thread((new Runnable() {
            @Override
            public void run() {
                Message msg = null;
                String progressBarData = "Génération de la nouvelle carte...";

                // populates the message
                msg = handlerTraitementRapide2.obtainMessage(MSG_IND, (Object) progressBarData);

                // sends the message to our handler
                handlerTraitementRapide2.sendMessage(msg);

                // starts the first long operation
                status = Traitement.updateSeuils();

                if (Accueil.ErrorStatus.NO_ERROR != status) {
                    Log.e("erreur2_tM", "error while computing the path status:"
                            + status);
                    // error management,creates an error message
                    msg = handlerTraitementRapide2.obtainMessage(MSG_ERR,
                            "error while computing the path status:"
                                    + status);
                    // sends the message to our handler
                    handlerTraitementRapide2.sendMessage(msg);
                } else {
                    //progressBarData="Succès";
                    msg = handlerTraitementRapide2.obtainMessage(MSG_CNF,
                            (Object) progressBarData);
                    // sends the message to our handler
                    handlerTraitementRapide2.sendMessage(msg);
                }
            }
        })).start();
    }
    final Handler handlerTraitementRapide2 = new Handler() {
        public void handleMessage(Message msg) {
            String text2display = null;
            switch (msg.what) {
                case MSG_IND:
                    if (mProgressDialog.isShowing()) {
                        mProgressDialog.setMessage(((String) msg.obj));
                    }
                    break;
                case MSG_ERR:
                    text2display = (String) msg.obj;
                    Toast.makeText(getApplicationContext(), "Error: " + text2display,
                            Toast.LENGTH_LONG).show();
                    if (mProgressDialog.isShowing()) {
                        mProgressDialog.dismiss();
                    }
                    break;
                case MSG_CNF:
                    //text2display = (String) msg.obj;
                    //Toast.makeText(mContext, "Info: " + text2display,
                    //        Toast.LENGTH_LONG).show();
                    if (mProgressDialog.isShowing()) {
                        mProgressDialog.dismiss();
                    }
                    // CARTE
                    nbPrediction = 0;
                    Intent intent = new Intent(Carte.this, Carte.class);
                    Log.d("Seuil4", Integer.toString(Seuil4));
                    // Extraction des seuils vers l'activité carte
                    intent.putExtra(EXTRA_SEUIL1, Seuil1);
                    intent.putExtra(EXTRA_SEUIL2, Seuil2);
                    intent.putExtra(EXTRA_SEUIL3, Seuil3);
                    intent.putExtra(EXTRA_SEUIL4, Seuil4);
                    //intent.putExtra(EXTRA_SEUIL5, Seuil5);
                    startActivity(intent);
                    break;
                default: // should never happen
                    break;
            }
        }
    };


    public void traitementMesures(){
        mProgressDialog = ProgressDialog.show(this, "Patientez...",
                "Long operation starts...", true);


        // useful code, variables declarations...
        new Thread((new Runnable() {
            @Override
            public void run() {
                Message msg = null;
                String progressBarData = "Calcul du quadrillage de la carte en cours...";

                // populates the message
                msg = handlerTraitementMesure.obtainMessage(MSG_IND, (Object) progressBarData);

                // sends the message to our handler
                handlerTraitementMesure.sendMessage(msg);

                // starts the first long operation
                status = Traitement.lectureData(nomFichierString);


                if (Accueil.ErrorStatus.NO_ERROR != status) {
                    Log.e("erreur1_tM", "error while parsing the file status:" + status);
                    // error management, creates an error message
                    msg = handlerTraitementMesure.obtainMessage(MSG_ERR,
                            "Granularité trop élevé par rapport à la zone parcourue. Longueur de la zone : " + Integer.toString(LENGTH_MAX_VALUE)+" m");
                    // sends the message to our handler
                    handlerTraitementMesure.sendMessage(msg);
                } else {
                    progressBarData = "Positionnement des cases sur la carte..";
                    //mProgressDialog.setMessage(progressBarData);

                    // populates the message
                    msg = handlerTraitementMesure.obtainMessage(MSG_IND,
                            (Object) progressBarData);

                    // sends the message to our handler
                    handlerTraitementMesure.sendMessage(msg);

                    status = Traitement.recupCoordCaseFract();


                    if (Accueil.ErrorStatus.NO_ERROR != status) {
                        Log.e("erreur1_tM", "error while parsing the file status:" + status);
                        // error management, creates an error message
                        msg = handlerTraitementMesure.obtainMessage(MSG_ERR,
                                "Granularité trop élevé par rapport à la zone parcourue. Longueur de la zone : " + Integer.toString(LENGTH_MAX_VALUE)+" m");
                        // sends the message to our handler
                        handlerTraitementMesure.sendMessage(msg);
                    } else {
                        progressBarData = "Positionnement des cases sur la carte..";
                        //mProgressDialog.setMessage(progressBarData);

                        // populates the message
                        msg = handlerTraitementMesure.obtainMessage(MSG_IND,
                                (Object) progressBarData);

                        // sends the message to our handler
                        handlerTraitementMesure.sendMessage(msg);
                    }


                    status = Traitement.lectureDataTemp1();

                    if (Accueil.ErrorStatus.NO_ERROR != status) {
                        Log.e("erreur1_tM", "error while parsing the file status:" + status);

                        // error management, creates an error message
                        msg = handlerTraitementMesure.obtainMessage(MSG_ERR,
                                "error while parsing the file status:" + status);
                        // sends the message to our handler
                        handlerTraitementMesure.sendMessage(msg);
                    } else {
                        progressBarData = "Recherche des moyennes maximales..."; // + pourcentage
                        //mProgressDialog.setMessage(progressBarData);

                        // populates the message
                        msg = handlerTraitementMesure.obtainMessage(MSG_IND,
                                (Object) progressBarData);

                        // sends the message to our handler
                        handlerTraitementMesure.sendMessage(msg);

                        status = Traitement.rechercheMoyMax();

                        if (Accueil.ErrorStatus.NO_ERROR != status) {
                            Log.e("erreur2_tM", "error while computing the path status:"
                                    + status);
                            // error management,creates an error message
                            msg = handlerTraitementMesure.obtainMessage(MSG_ERR,
                                    "error while computing the path status:"
                                            + status);
                            // sends the message to our handler
                            handlerTraitementMesure.sendMessage(msg);
                        } else {
                            //progressBarData="Succès";
                            msg = handlerTraitementMesure.obtainMessage(MSG_IND,
                                    (Object) progressBarData);
                            // sends the message to our handler
                            handlerTraitementMesure.sendMessage(msg);

                            status = Traitement.colorCases2();

                            if (Accueil.ErrorStatus.NO_ERROR != status) {
                                Log.e("erreur2_tM", "error while computing the path status:"
                                        + status);
                                // error management,creates an error message
                                msg = handlerTraitementMesure.obtainMessage(MSG_ERR,
                                        "error while computing the path status:"
                                                + status);
                                // sends the message to our handler
                                handlerTraitementMesure.sendMessage(msg);
                            } else {
                                //progressBarData="Succès";
                                msg = handlerTraitementMesure.obtainMessage(MSG_CNF,
                                        (Object) progressBarData);
                                // sends the message to our handler
                                handlerTraitementMesure.sendMessage(msg);
                            }

                        }
                    }
                }
            }
        })).start();
    }

    final Handler handlerTraitementMesure = new Handler() {
        public void handleMessage(Message msg) {
            String text2display = null;
            switch (msg.what) {
                case MSG_IND:
                    if (mProgressDialog.isShowing()) {
                        mProgressDialog.setMessage(((String) msg.obj));
                    }
                    break;
                case MSG_ERR:
                    text2display = (String) msg.obj;
                    Toast.makeText(contextActif, "Erreur : " + text2display,
                            Toast.LENGTH_LONG).show();
                    if (mProgressDialog.isShowing()) {
                        mProgressDialog.dismiss();
                    }
                    break;
                case MSG_CNF:
                    //text2display = (String) msg.obj;
                    //Toast.makeText(mContext, "Info: " + text2display,
                    //        Toast.LENGTH_LONG).show();
                    if (mProgressDialog.isShowing()) {
                        mProgressDialog.dismiss();
                    }
                    //anciennemesurebool = true;
                    nbPrediction = 0;
                    Intent refreshCarteMacExterne = new Intent(Carte.this, Carte.class);
                    startActivity(refreshCarteMacExterne );
                    break;
                default: // should never happen
                    break;
            }
        }
    };

    public void lectureFichier(){
        mProgressDialog = ProgressDialog.show(this, "Patientez...",
                "Long operation starts...", true);


        // useful code, variables declarations...
        new Thread((new Runnable() {
            @Override
            public void run() {
                Message msg = null;
                String progressBarData = "Récupération des données";

                // populates the message
                msg = handlerLectureFichier.obtainMessage(MSG_IND, (Object) progressBarData);

                // sends the message to our handler
                handlerLectureFichier.sendMessage(msg);

                // starts the first operation

                status = Traitement.lectureData(nomFichierString);


                if (Accueil.ErrorStatus.NO_ERROR != status) {
                    Log.e("erreur1_tM", "error while parsing the file status:" + status);
                    // error management, creates an error message
                    msg = handlerLectureFichier.obtainMessage(MSG_ERR,
                            "Granularité trop élevé par rapport à la zone parcourue. Longueur de la zone : " + Integer.toString(LENGTH_MAX_VALUE)+" m");
                    // sends the message to our handler
                    handlerLectureFichier.sendMessage(msg);
                } else {
                    //progressBarData="Succès";
                    msg = handlerLectureFichier.obtainMessage(MSG_CNF,
                            (Object) progressBarData);
                    // sends the message to our handler
                    handlerLectureFichier.sendMessage(msg);

                }
            }
        })).start();
    }

    final Handler handlerLectureFichier = new Handler() {
        public void handleMessage(Message msg) {
            String text2display = null;
            switch (msg.what) {
                case MSG_IND:
                    if (mProgressDialog.isShowing()) {
                        mProgressDialog.setMessage(((String) msg.obj));
                    }
                    break;
                case MSG_ERR:
                    text2display = (String) msg.obj;
                    Toast.makeText(contextActif, "Erreur : " + text2display,
                            Toast.LENGTH_LONG).show();
                    if (mProgressDialog.isShowing()) {
                        mProgressDialog.dismiss();
                    }
                    break;
                case MSG_CNF:
                    if (mProgressDialog.isShowing()) {
                        mProgressDialog.dismiss();
                    }
                    popUpMAC();
                    break;
                default: // should never happen
                    break;
            }
        }
    };

    private Accueil.ErrorStatus popUpMAC(){
        final View alertDialogViewChoixMac = alertdialogChoixMac.inflate(R.layout.alertdialog_choix_mac, null);
        final AlertDialog.Builder alertdial = new AlertDialog.Builder(Carte.this);
        alertdial.setView(alertDialogViewChoixMac);
        alertdial.setTitle("Selection des émetteurs WIFI");

        // RadioGroup
        final RadioGroup radioGroup3=(RadioGroup) alertDialogViewChoixMac.findViewById(R.id.radioGroup3);
        final RadioGroup radioGroup4=(RadioGroup) alertDialogViewChoixMac.findViewById(R.id.radioGroup4);
        final RadioButton radio_oui = (RadioButton) alertDialogViewChoixMac.findViewById(R.id.radio_oui);
        final RadioButton radio_non = (RadioButton) alertDialogViewChoixMac.findViewById(R.id.radio_non);
        final RadioButton radio_oui2 = (RadioButton) alertDialogViewChoixMac.findViewById(R.id.radio_oui2);
        final RadioButton radio_non2 = (RadioButton) alertDialogViewChoixMac.findViewById(R.id.radio_non2);

        // Spinner
        final MultiSelectionSpinnerMacChoisies spinnerMACSSID1 = (MultiSelectionSpinnerMacChoisies) alertDialogViewChoixMac.findViewById(R.id.spinner_mac_SSID1);
        final MultiSelectionSpinner spinnerSSID2 = (MultiSelectionSpinner) alertDialogViewChoixMac.findViewById(R.id.spinner_SSID2);
        final MultiSelectionSpinnerMacAjout spinnerMACSSID2 = (MultiSelectionSpinnerMacAjout) alertDialogViewChoixMac.findViewById(R.id.spinner_mac_SSID2);

        // TextView
        final TextView choixSSID2Texte = (TextView) alertDialogViewChoixMac.findViewById(R.id.choixSSID2Texte);
        final TextView choixMAC2Texte = (TextView) alertDialogViewChoixMac.findViewById(R.id.choixMAC2Texte);
        final TextView decocherMACTexte = (TextView) alertDialogViewChoixMac.findViewById(R.id.decocherMacTexte);
        final TextView indicationSSID = (TextView) alertDialogViewChoixMac.findViewById(R.id.indicationSSID);

        indicationSSID.setText(choixSSID);
        spinnerMACSSID1.setVisibility(View.GONE);
        spinnerSSID2.setVisibility(View.GONE);
        spinnerMACSSID2.setVisibility(View.GONE);
        choixSSID2Texte.setVisibility(View.GONE);
        choixMAC2Texte.setVisibility(View.GONE);
        decocherMACTexte.setVisibility(View.GONE);
        spinnerMACSSID1.setItems(choixMac);
        spinnerMACSSID1.setSelection(choixMac);

        // SSID externe
        List<String> Multilist2 = new ArrayList<String>();
        for (int i = 3; i < Traitement.nrow() - 3; i++) {                   //Commence à 3 car les 3 premières lignes sont des informations non utiles
            if (!Multilist2.contains(Traitement.wordAt(i, 5)) && !Traitement.wordAt(i, 5).equals("") ) {
                Multilist2.add(Traitement.wordAt(i, 5));        //6ème colonne nom @MAC
            }
        }
        spinnerSSID2.setItems(Multilist2);

        radioGroup3.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.radio_oui:
                        spinnerMACSSID1.setVisibility(View.VISIBLE);
                        decocherMACTexte.setVisibility(View.VISIBLE);
                        choixMAC1boolean=true;
                        break;
                    case R.id.radio_non:
                        spinnerMACSSID1.setVisibility(View.GONE);
                        decocherMACTexte.setVisibility(View.GONE);
                        choixMAC1boolean=false;
                        break;
                    default:
                        break;
                }
            }
        });

        radioGroup4.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.radio_oui2:
                        choixSSID2boolean=true;
                        spinnerSSID2.setVisibility(View.VISIBLE);
                        choixSSID2Texte.setVisibility(View.VISIBLE);
                        choixMAC2Texte.setVisibility(View.VISIBLE);

                        List ListeTemporaire = new ArrayList();
                        ListeTemporaire.add("Selectionnez un ou plusieurs réseaux");
                        spinnerMACSSID2.setItems(ListeTemporaire);
                        spinnerMACSSID2.setVisibility(View.VISIBLE);
                        break;
                    case R.id.radio_non2:
                        choixSSID2boolean=false;
                        spinnerMACSSID1.setVisibility(View.GONE);
                        break;
                    default:
                        break;
                }
            }
        });



        // Enregistrement de la configuration
        alertdial.setPositiveButton("Enregistrer", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                if(radio_non.isChecked() && radio_non2.isChecked()) {

                } else {
                    choixMacMAJBoolean=true;
                    if(radio_oui2.isChecked()) {
                        if (choixMacReseauExterne.size() != 0) {
                            for (int i = 0; i < choixMacReseauExterne.size(); i++) {
                                if (!choixMac.contains(choixMacReseauExterne.get(i))) {
                                    choixMac.add(choixMacReseauExterne.get(i));
                                    Log.d("AjoutMac", choixMacReseauExterne.get(i).toString());
                                }
                            }
                        }
                    }
                    File fichiersTemp = new File(Environment.getExternalStorageDirectory() + File.separator + "WifiStateData" + File.separator + "FichiersTemp");
                     File[] listFiles = fichiersTemp.listFiles();

                    if (fichiersTemp.exists()) {
                        for (int i = 0; i < listFiles.length; i++) {
                            listFiles[i].delete();
                        }
                    }
                    traitementMesures();
                }

            }
        });

        alertdial.setOnCancelListener(new DialogInterface.OnCancelListener() {

            @Override
            public void onCancel(DialogInterface dialog) {
                Traitement.RAZdata();
            }
        });

        alertdial.setOnDismissListener(new DialogInterface.OnDismissListener() {

            @Override
            public void onDismiss(DialogInterface dialog) {
                Traitement.RAZdata();
            }
        });
        alertdial.setIcon(android.R.drawable.ic_dialog_info);
        alertdial.show();
        onPause();


        return Accueil.ErrorStatus.NO_ERROR;
    }

    @Override
    public void onBackPressed (){
        razCasesParcouruesSansPrediction();
        nbPrediction=0;
        prediTextView.setText("Aucune case prédite");
        Intent backAccueil = new Intent(Carte.this, Accueil.class);
        startActivity(backAccueil);
    }
}





