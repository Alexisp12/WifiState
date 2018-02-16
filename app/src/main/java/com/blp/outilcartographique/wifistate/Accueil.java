package com.blp.outilcartographique.wifistate;

/**
 * Created by Alexis on 19/05/2017.
 */

        import android.app.Activity;
        import android.app.AlertDialog;
        import android.app.ProgressDialog;
        import android.content.Context;
        import android.content.DialogInterface;
        import android.content.Intent;
        import android.content.SharedPreferences;
        import android.os.Bundle;
        import android.os.Environment;
        import android.os.Handler;
        import android.os.Looper;
        import android.os.Message;
        import android.util.Log;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.widget.*;
        import android.widget.AdapterView.OnItemSelectedListener;
        import java.io.File;
        import java.text.DecimalFormat;
        import java.util.ArrayList;
        import java.util.List;
        import static com.blp.outilcartographique.wifistate.Acquisition_mesure_continue.NbMesures;
        import static com.blp.outilcartographique.wifistate.Acquisition_mesure_continue.SETS;
        import static com.blp.outilcartographique.wifistate.Acquisition_mesure_continue.nomCampagneString;
        import static com.blp.outilcartographique.wifistate.Acquisition_mesure_continue.nomFichierString;
        import static com.blp.outilcartographique.wifistate.Acquisition_mesure_continue.nombreEchantillonTotal;
        import static com.blp.outilcartographique.wifistate.Acquisition_mesure_continue.renameOk;
        import static com.blp.outilcartographique.wifistate.Carte.choixMacMAJBoolean;
        import static com.blp.outilcartographique.wifistate.Traitement.LENGTH_MAX_VALUE;


/**
 * Created by yohannlebourlout on 28/01/2017.
 */

public class Accueil extends Activity {
    public static Context contextActif;
    public static Toast toast;
    public static List listeMacAutresReseaux=new ArrayList<String>();
    public static List<String> choixMac;
    public static List<String> choixCanal;
    public static List<String> choixMacReseauExterne;
    private List Listhotspot;
    public static boolean accueilActif=false;
    public static boolean carteActif=false;
    public static boolean switchMACActif=false;
    public static boolean switchCanalActif=false;
    public static boolean nouvelleCampagneBool;
    public static boolean continuercollectbool=false;
    public static boolean discontinuebool=false;
    public static boolean continuebool=false;
    public static int SeuilPasDeValeur=-200;
    public static int nbPrediction=0;
    public static int Seuil1=-70;
    public static int Seuil2=-75;
    public static int Seuil3=-80;
    public static int Seuil4=-85;
    public static int Seuil5=10;
    protected ProgressDialog mProgressDialog;
    private ErrorStatus status;
    private EditText EditSeuil5;
    public static String Couleur;
    public static String choixSSID;
    public static String txt = ".txt"; // Lié à la concaténation
    private String[] files;
    private String noname = "Sans nom";
    public static String noEmetteur = "Aucun émetteur supplémentaire trouvé";
    enum ErrorStatus {
        NO_ERROR, GRANUL_ELEVE
    };
    public static final int MSG_ERR = 0;
    public static final int MSG_CNF = 1;
    public static final int MSG_IND = 2;
    private String[] nomCampagnes;
    private ImageButton nouvelleCampagne;
    private ImageButton afficherLaCarte;
    private ImageButton CollecteContinuer;
    private ImageButton Dropbox;
    private int ValSeuil5= 10;
    private LayoutInflater alertdialogCollect;
    private LayoutInflater alertdialogCollect2;
    private LayoutInflater alertdialogMode;
    private File repertoire;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.accueil);

        contextActif=getApplicationContext();
        accueilActif=true;
        carteActif = false;

        // Bouton d'accueil
        nouvelleCampagne = (ImageButton) findViewById(R.id.Collecte);
        afficherLaCarte = (ImageButton) findViewById(R.id.afficherLaCarte);
        CollecteContinuer = (ImageButton) findViewById(R.id.CollecteContinuer);
        Dropbox = (ImageButton) findViewById(R.id.Dropbox);

        /*Alerte dialogue*/
        alertdialogCollect = LayoutInflater.from(this);
        alertdialogCollect2 = LayoutInflater.from(this);
        alertdialogMode = LayoutInflater.from(this);

        // En cliquant sur le bouton "Nouvelle campagne"
        nouvelleCampagne.setOnClickListener(new View.OnClickListener() {
            public void onClick(View collect) {
                final View alertDialogViewMesureMode = alertdialogMode.inflate(R.layout.alertdialog_mesure_mode, null);

                AlertDialog.Builder alertdial = new AlertDialog.Builder(Accueil.this);
                alertdial.setView(alertDialogViewMesureMode);
                alertdial.setTitle("Nouvelle campagne");
                final RadioGroup radioGroup=(RadioGroup) alertDialogViewMesureMode.findViewById(R.id.radioGroup);
                final RadioButton radiobuttonContinue =(RadioButton)alertDialogViewMesureMode.findViewById(R.id.radio_continue);
                radiobuttonContinue.setChecked(true);
                continuebool=true;
                discontinuebool=false;
                radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                        switch(checkedId) {
                            case R.id.radio_discontinue :
                                discontinuebool=true;
                                continuebool=false;
                                break;
                            case R.id.radio_continue :
                                continuebool=true;
                                discontinuebool=false;
                                break;
                            default:
                                break;
                        }
                    }

                });

                // Lorsque l'utilisateur valide en cliquant sur "Choisir"
                alertdial.setPositiveButton("Choisir", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Le mode discontinue n'est plus intéressant et obsolète
                        if(discontinuebool) {
                            Toast toast = Toast.makeText(getApplicationContext(),"Mode obsolète", Toast.LENGTH_SHORT);
                            toast.show();
//                            // Configuration nouvelle collecte
//                            final View alertDialogViewNouvelleCollecte = alertdialogCollect.inflate(R.layout.alertdialog_nouvelle_collecte2, null);
//
//                            AlertDialog.Builder alertdial = new AlertDialog.Builder(Accueil.this);
//                            alertdial.setView(alertDialogViewNouvelleCollecte);
//                            alertdial.setTitle("Nouvelle campagne");
//                            final EditText nomCampagneEdit = (EditText) alertDialogViewNouvelleCollecte.findViewById(R.id.EditText1);
//                            final EditText nombreEchantillon = (EditText) alertDialogViewNouvelleCollecte.findViewById(R.id.EditText2);
//                            final SeekBar seekBar = (SeekBar) alertDialogViewNouvelleCollecte.findViewById(R.id.seekBar);
//                            seekBar.setProgress(1);
//
//                            SeekBar.OnSeekBarChangeListener SeekBarListener =
//                                    new SeekBar.OnSeekBarChangeListener() {
//                                        @Override
//                                        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//                                            nombreEchantillon.setText(Integer.toString(progress));
//                                        }
//
//                                        @Override
//                                        public void onStartTrackingTouch(SeekBar seekBar) {
//
//                                        }
//
//                                        @Override
//                                        public void onStopTrackingTouch(SeekBar seekBar) {
//
//                                        }
//                                    };
//
//                            seekBar.setOnSeekBarChangeListener(SeekBarListener);
//                            // Enregistrement de la configuration
//                            alertdial.setPositiveButton("Enregistrer", new DialogInterface.OnClickListener()
//
//                                    {
//                                        public void onClick(DialogInterface dialog, int which) {
//                                            repertoire = new File(Environment.getExternalStorageDirectory() + File.separator + "WifiStateData");
//                                            files = repertoire.list();
//                                            nouvelleCampagneBool = true;
//                                            if (repertoire.exists()) {
//                                                if (nomCampagneEdit.getText().length() == 0) {
//                                                    for (int i = 0; i < files.length; i++) {
//                                                        if (files[i].equals(noname + txt)) {
//                                                            renameOk = false;
//                                                        }
//                                                    }
//                                                } else {
//                                                    for (int i = 0; i < files.length; i++) {
//                                                        if (files[i].equals(nomCampagneEdit.getText().toString() + txt)) {
//                                                            renameOk = false;
//                                                        }
//                                                    }
//                                                }
//                                            }
//
//                                            if (renameOk) {
//                                                if (nomCampagneEdit.getText().length() == 0) {
//                                                    nomCampagneString = noname;
//                                                } else {
//                                                    nomCampagneString = nomCampagneEdit.getText().toString();
//                                                }
//                                                if (nombreEchantillon.getText().length() == 0) {
//                                                    nombreEchantillonTotal = nombreEchantillonTotalDefaut;
//                                                } else {
//                                                    nombreEchantillonTotal = (Integer.parseInt(nombreEchantillon.getText().toString())) - 1;
//                                                }
//
//
//
//                                                /* RAZ du nombre de mesure */
//                                                NbMesures = 1;
//
//                                                /* Nom du fichier de data*/
//                                                nomFichierString = nomCampagneString + txt;
//
//                                                /* Récupération des données */
//                                                SharedPreferences settings = getSharedPreferences(SETS, 0);
//
//                                                /* Incorparation des nouvelles données dans les préférences*/
//                                                SharedPreferences.Editor editor = settings.edit();
//                                                editor.putInt("NbMesures", NbMesures);
//                                                editor.putString("nomCampagneString", nomCampagneString);
//                                                editor.putString("nomFichier", nomFichierString);
//                                                editor.putInt("nombreEchantillonTotal", nombreEchantillonTotal);
//                                                editor.putBoolean("continuebool",continuebool);
//                                                editor.putBoolean("discontinuebool",discontinuebool);
//                                                editor.commit();
//
//                                                Intent intent = new Intent(Accueil.this, Acquisition_mesure_discontinue.class);
//                                                startActivity(intent);
//
//                                            } else {
//                                                renameOk = true;
//                                                AlertDialog.Builder alertdial = new AlertDialog.Builder(Accueil.this);
//                                                alertdial.setTitle("Un fichier portant ce nom existe déjà");
//                                                alertdial.setMessage("Choisissez un nom de campagne qui n'a pas déjà été utilisé");
//                                                alertdial.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
//                                                    public void onClick(DialogInterface dialog, int which) {
//
//                                                    }
//                                                });
//                                                alertdial.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
//                                                    public void onClick(DialogInterface dialog, int which) {
//
//                                                    }
//                                                });
//                                                alertdial.setIcon(android.R.drawable.ic_dialog_alert);
//                                                alertdial.show();
//                                            }
//                                        }
//                                    }
//
//                            );
//                            alertdial.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener()
//
//                                    {
//                                        public void onClick(DialogInterface dialog, int which) {
//                                            // On ne fais rien si l'utilisateur clique sur annuler
//                                        }
//                                    }
//
//                            );
//                            alertdial.setIcon(android.R.drawable.ic_dialog_info);
//                            alertdial.show();
                        }

                        // Mode continue
                        if(continuebool){
                            // Pop-up pour définir les paramètres de la nouvelle campagne
                            final View alertDialogViewNouvelleCollecte = alertdialogCollect.inflate(R.layout.alertdialog_nouvelle_collecte2, null);
                            AlertDialog.Builder alertdial = new AlertDialog.Builder(Accueil.this);
                            alertdial.setView(alertDialogViewNouvelleCollecte);
                            alertdial.setTitle("Nouvelle campagne");
                            final EditText nomCampagneEdit = (EditText) alertDialogViewNouvelleCollecte.findViewById(R.id.EditText1);
                            final EditText nombreEchantillon = (EditText) alertDialogViewNouvelleCollecte.findViewById(R.id.EditText2);
                            final SeekBar seekBar = (SeekBar) alertDialogViewNouvelleCollecte.findViewById(R.id.seekBar);
                            final TextView questionEchantillon = (TextView) alertDialogViewNouvelleCollecte.findViewById(R.id.TextView2);
                            final TextView infoEchantillon = (TextView) alertDialogViewNouvelleCollecte.findViewById(R.id.Info);

                            // On cache certains éléments au départ
                            seekBar.setVisibility(View.GONE);
                            nombreEchantillon.setVisibility(View.GONE);
                            questionEchantillon.setVisibility(View.GONE);
                            infoEchantillon.setVisibility(View.GONE);
                            nombreEchantillon.setVisibility(View.GONE);


                            // Gestion du nombre d'échantillon
                            SeekBar.OnSeekBarChangeListener SeekBarListener =
                                    new SeekBar.OnSeekBarChangeListener() {
                                        @Override
                                        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                                            nombreEchantillon.setText(Integer.toString(progress));
                                        }

                                        @Override
                                        public void onStartTrackingTouch(SeekBar seekBar) {

                                        }

                                        @Override
                                        public void onStopTrackingTouch(SeekBar seekBar) {

                                        }
                                    };

                            seekBar.setOnSeekBarChangeListener(SeekBarListener);

                            // Enregistrement de la configuration
                            alertdial.setPositiveButton("Enregistrer", new DialogInterface.OnClickListener()
                                    {
                                        public void onClick(DialogInterface dialog, int which) {
                                            repertoire = new File(Environment.getExternalStorageDirectory() + File.separator + "WifiStateData");
                                            files = repertoire.list();
                                            nouvelleCampagneBool = true;
                                            if (repertoire.exists()) {
                                                if (nomCampagneEdit.getText().length() == 0) {
                                                    for (int i = 0; i < files.length; i++) {
                                                        if (files[i].equals(noname + txt)) {
                                                            renameOk = false;
                                                        }
                                                    }
                                                } else {
                                                    for (int i = 0; i < files.length; i++) {
                                                        if (files[i].equals(nomCampagneEdit.getText().toString() + txt)) {
                                                            renameOk = false;
                                                        }
                                                    }
                                                }
                                            }

                                            if (renameOk) {
                                                if (nomCampagneEdit.getText().length() == 0) {
                                                    nomCampagneString = noname;
                                                } else {
                                                    nomCampagneString = nomCampagneEdit.getText().toString();
                                                }

                                                /* RAZ du nombre de mesure */
                                                NbMesures = 1;

                                                /* Nom du fichier de data*/
                                                nomFichierString = nomCampagneString + txt;

                                                /* Récupération des données */
                                                SharedPreferences settings = getSharedPreferences(SETS, 0);

                                                /* Incorparation des nouvelles données dans les préférences*/
                                                SharedPreferences.Editor editor = settings.edit();
                                                editor.putInt("NbMesures", NbMesures);
                                                editor.putString("nomCampagneString", nomCampagneString);
                                                editor.putString("nomFichier", nomFichierString);
                                                editor.putBoolean("continuebool",continuebool);
                                                editor.putBoolean("discontinuebool",discontinuebool);
                                                editor.commit();
                                                // Lancement de l'activité Acquisition
                                                Intent intent = new Intent(Accueil.this, Acquisition_mesure_continue.class);
                                                startActivity(intent);
                                            } else {
                                                renameOk = true;
                                                AlertDialog.Builder alertdial = new AlertDialog.Builder(Accueil.this);
                                                alertdial.setTitle("Un fichier portant ce nom existe déjà");
                                                alertdial.setMessage("Choisissez un nom de campagne qui n'a pas déjà été utilisé");
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

                            );
                            alertdial.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener()
                                    {
                                        public void onClick(DialogInterface dialog, int which) {
                                            // On ne fais rien si l'utilisateur clique sur annuler
                                        }
                                    }

                            );
                            alertdial.setIcon(android.R.drawable.ic_dialog_info);
                            alertdial.show();

                        }
                    }
                });
                alertdial.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                alertdial.setIcon(android.R.drawable.ic_dialog_info);
                alertdial.show();
            }
        });

        // Gestion du bouton "afficher la carte"
        afficherLaCarte.setOnClickListener(new View.OnClickListener() {
            public void onClick(View collect) {
                final View alertDialogViewChoixCampagne2 = alertdialogCollect2.inflate(R.layout.alertdialog_choix_campagne2, null);
                final AlertDialog.Builder alertdial = new AlertDialog.Builder(Accueil.this);
                alertdial.setView(alertDialogViewChoixCampagne2);
                alertdial.setTitle("Affichage d'une campagne de mesures");

                // Recherche des fichiers textes présents sur la tablette (dossier WifiStateData)
                Traitement.findNamesCampagnes();
                nomCampagnes = Traitement.getNomsCampagnes();

                final SeekBar.OnSeekBarChangeListener SeekBarListener5 =
                        new SeekBar.OnSeekBarChangeListener() {
                            @Override
                            public void onProgressChanged (SeekBar seekBar, int progress, boolean fromUser) {
                                ValSeuil5=progress;
                                final EditText EditSeuil5 = (EditText) alertDialogViewChoixCampagne2.findViewById(R.id.EditSeuil5);
                                EditSeuil5.setText(Integer.toString(ValSeuil5));
                            }

                            @Override
                            public void onStartTrackingTouch(SeekBar seekBar) {

                            }

                            @Override
                            public void onStopTrackingTouch(SeekBar seekBar) {

                            }
                        };
                SeekBar SeekBar5 = (SeekBar) alertDialogViewChoixCampagne2.findViewById(R.id.seekBar5);
                SeekBar5.setOnSeekBarChangeListener(SeekBarListener5);

                EditSeuil5 = (EditText) alertDialogViewChoixCampagne2.findViewById(R.id.EditSeuil5);
                EditSeuil5.setText("10");
                SeekBar5.setProgress(10);

                final Spinner spinner = (Spinner)alertDialogViewChoixCampagne2.findViewById(R.id.spinner);
                List ListCampagne = new ArrayList();
                if(nomCampagnes!=null) {
                    for (int i = 0; i < Traitement.getNombreFichiers(); i++) {
                        for (int j = 0; j < nomCampagnes[i].length(); j++) {
                            if (nomCampagnes[i].charAt(j) == '.') {
                                if (!nomCampagnes[i].substring(0, j).equals("")) {
                                    ListCampagne.add(nomCampagnes[i].substring(0, j));
                                }
                            }
                        }
                    }
                } else {
                    ListCampagne.add("Aucune campagne n'a été trouvé");
                }
		        /*Le Spinner a besoin d'un adapter pour sa presentation alors on lui passe le context(this) et
                un fichier de presentation par défaut( android.R.layout.simple_spinner_item)
		        Avec la liste des elements (exemple) */
                ArrayAdapter adapter = new ArrayAdapter(
                        Accueil.this,
                        android.R.layout.simple_spinner_item,
                        ListCampagne
                );
                /*On definit une présentation du spinner quand il est déroulé         (android.R.layout.simple_spinner_dropdown_item) */
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                //Enfin on passe l'adapter au Spinner et c'est tout
                spinner.setAdapter(adapter);
                final Spinner spinnerchoixSSID = (Spinner) alertDialogViewChoixCampagne2.findViewById(R.id.spinnerChoixSSID);
                List ListeTemporaire = new ArrayList();
                ListeTemporaire.add("Updating..");

                ArrayAdapter adapter2 = new ArrayAdapter(
                        Accueil.this,
                        android.R.layout.simple_spinner_item,
                        ListeTemporaire
                );

                /* On definit une présentation du spinner quand il est déroulé         (android.R.layout.simple_spinner_dropdown_item) */
                adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                //Enfin on passe l'adapter au Spinner et c'est tout
                spinnerchoixSSID.setAdapter(adapter2);
                // Lorsqu'une campagne est séléctionné, récupération du nom de tout les réseaux le composant
                spinner.setOnItemSelectedListener(
                        new OnItemSelectedListener() {
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                Toast toast = Toast.makeText(getApplicationContext(), "La liste des réseaux à été actualisé.", Toast.LENGTH_SHORT);
                                toast.show();
                                if(spinner.getItemAtPosition(position)!=null) {
                                    nomFichierString = spinner.getSelectedItem().toString() + txt;
                                    nomCampagneString = spinner.getSelectedItem().toString();
                                    Traitement.lectureData(nomFichierString);
                                    Listhotspot = new ArrayList();
                                    Listhotspot.clear();
                                    for (int i = 3; i < Traitement.nrow() - 3; i++) {                   //Commence à 3 car les 3 premières lignes sont des informations non utiles
                                        if (!Listhotspot.contains(Traitement.wordAt(i, 5)) && !Traitement.wordAt(i, 5).equals("")) {
                                            Listhotspot.add(Traitement.wordAt(i, 5));        //5ème colonne nom du SSID
                                        }
                                    }

                                    ArrayAdapter adapter2 = new ArrayAdapter(
                                            Accueil.this,
                                            android.R.layout.simple_spinner_item,
                                            Listhotspot
                                    );

                                    /* On definit une présentation du spinner quand il est déroulé (android.R.layout.simple_spinner_dropdown_item) */
                                    adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                    //On passe l'adapter au Spinner
                                    spinnerchoixSSID.setAdapter(adapter2);
                                }
                            }
                            @Override
                            public void onNothingSelected(AdapterView<?> arg0) {
                                // TODO Auto-generated method stub
                            }
                        }
                );

                // Enregistrement de la configuration
                alertdial.setPositiveButton("Afficher la carte", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        nomFichierString=spinner.getSelectedItem().toString()+txt;
                        nomCampagneString=spinner.getSelectedItem().toString();
                        if(Integer.parseInt(EditSeuil5.getText().toString())==0){
                            Seuil5=1;
                        } else {
                            Seuil5 = Integer.parseInt(EditSeuil5.getText().toString());
                        }
                        choixSSID = spinnerchoixSSID.getSelectedItem().toString();
                        choixMacMAJBoolean = false;

                        /* Insertion des données */
                        SharedPreferences settings = getSharedPreferences(SETS, 0);

                        /* Incorparation des nouvelles données dans les préférences*/
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString("nomCampagneString", nomCampagneString);
                        editor.putString("nomFichier", nomFichierString);
                        editor.commit();

                        File fichiersTemp = new File(Environment.getExternalStorageDirectory() + File.separator + "WifiStateData" + File.separator + "FichiersTemp");
                        File [] listFiles=fichiersTemp.listFiles();

                        if(fichiersTemp.exists()) {
                            for (int i = 0; i < listFiles.length; i++) {
                                listFiles[i].delete();
                            }
                        }
                        traitementMesures();
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

        // Gestion du bouton "Dropbox"
        Dropbox.setOnClickListener(new View.OnClickListener() {
            public void onClick(View collect) {
                // Lancement de l'activité Dropbox
                Intent intent = new Intent(Accueil.this, MainActivityDropbox.class);
                startActivity(intent);
            }
        });

        // Gestion du bouton "Continuer une campagne"
        CollecteContinuer.setOnClickListener(new View.OnClickListener() {
            public void onClick(View collect) {
                final View alertDialogViewChoixCampagne = alertdialogCollect.inflate(R.layout.alertdialog_choix_campagne, null);
                AlertDialog.Builder alertdial = new AlertDialog.Builder(Accueil.this);
                alertdial.setView(alertDialogViewChoixCampagne);
                alertdial.setTitle("Continuer une campagne de mesures");
                Traitement.findNamesCampagnes();

                nomCampagnes = Traitement.getNomsCampagnes();

                final Spinner spinner = (Spinner)alertDialogViewChoixCampagne.findViewById(R.id.spinner);

                //Création d'une liste d'élément à mettre dans le Spinner(pour l'exemple)
                List exempleList = new ArrayList();
                for(int i=0;i<Traitement.getNombreFichiers();i++){
                    for(int j=0;j<nomCampagnes[i].length();j++){
                        if(nomCampagnes[i].charAt(j) == '.'){
                            if(!(nomCampagnes[i].substring(0,j)).equals("")) {
                                exempleList.add(nomCampagnes[i].substring(0, j));
                            }
                        }
                    }
                }

		        /*Le Spinner a besoin d'un adapter pour sa presentation alors on lui passe le context(this) et
                un fichier de presentation par défaut( android.R.layout.simple_spinner_item)
		        Avec la liste des elements (exemple) */
                ArrayAdapter adapter = new ArrayAdapter(
                        Accueil.this,
                        android.R.layout.simple_spinner_item,
                        exempleList
                );

                /* On definit une présentation du spinner quand il est déroulé         (android.R.layout.simple_spinner_dropdown_item) */
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner.setAdapter(adapter);
                final RadioGroup radioGroup=(RadioGroup) alertDialogViewChoixCampagne.findViewById(R.id.radioGroup);
                final SeekBar seekBar = (SeekBar) alertDialogViewChoixCampagne.findViewById(R.id.seekBar);
                final EditText nombreEchantillon = (EditText) alertDialogViewChoixCampagne.findViewById(R.id.EditText2);
                final TextView nbEchantillonTextView = (TextView) alertDialogViewChoixCampagne.findViewById(R.id.TextView2);
                final RadioButton radiobuttonContinue =(RadioButton)alertDialogViewChoixCampagne.findViewById(R.id.radio_continue);
                nombreEchantillon.setVisibility(View.GONE);
                seekBar.setVisibility(View.GONE);
                nbEchantillonTextView.setVisibility(View.GONE);
                radiobuttonContinue.setChecked(true);
                continuebool=true;

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

                                SeekBar.OnSeekBarChangeListener SeekBarListener =
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
                                //nombreEchantillon.setText(Integer.toString(nombreEchantillonTotal+1));

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

                // Enregistrement de la configuration
                alertdial.setPositiveButton("Choisir", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if(continuebool) {
                            Intent intent = new Intent(Accueil.this, Acquisition_mesure_continue.class);
                            nomFichierString = spinner.getSelectedItem().toString() + txt;
                            nomCampagneString = spinner.getSelectedItem().toString();
                            Traitement.lectureData(nomFichierString);
                            NbMesures = Integer.parseInt(Traitement.wordAt(Traitement.nrow() - 1, 0)) + 1;

                            /* Récupération des données */
                            SharedPreferences settings = getSharedPreferences(SETS, 0);

                            /* Incorparation des nouvelles données dans les préférences*/
                            SharedPreferences.Editor editor = settings.edit();
                            editor.putInt("NbMesures", NbMesures);
                            editor.putString("nomCampagneString", nomCampagneString);
                            editor.putString("nomFichier", nomFichierString);
                            editor.putBoolean("continuebool", continuebool);
                            editor.putBoolean("discontinuebool", discontinuebool);
                            editor.commit();

                            Log.d("NomFichierString", nomFichierString);
                            continuercollectbool = true;
                            startActivity(intent);
                        }
                        if(discontinuebool) {
                            Toast toast = Toast.makeText(getApplicationContext(),"Mode obsolète", Toast.LENGTH_SHORT);
                            toast.show();
                        }
                    }
                });
                alertdial.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // On ne fais rien si l'utilisateur clique sur annuler
                    }
                });
                alertdial.setIcon(android.R.drawable.ic_dialog_info);
                alertdial.show();
            }
        });

    }

    public void traitementMesures(){
        // Gestion de la progression de l'alert dialog
        mProgressDialog = ProgressDialog.show(this, "Patientez...",
                "Long operation starts...", true);
        new Thread((new Runnable() {
            @Override
            public void run() {
                Message msg = null;
                String progressBarData = "Calcul du quadrillage de la carte en cours...";
                // populates the message
                msg = traitementMesureHandler.obtainMessage(MSG_IND, (Object) progressBarData);
                // sends the message to our handler
                traitementMesureHandler.sendMessage(msg);
                // starts the first operation
                status = Traitement.recupCoordCaseFract();
                if (Accueil.ErrorStatus.NO_ERROR != status) {
                    Log.e("erreur1_tM", "error while parsing the file status:" + status);
                    // error management, creates an error message
                    msg = traitementMesureHandler.obtainMessage(MSG_ERR,
                            "Granularité trop élevé par rapport à la zone parcourue. Longueur de la zone : " + Integer.toString(LENGTH_MAX_VALUE)+" m");
                    // sends the message to our handler
                    traitementMesureHandler.sendMessage(msg);
                } else {
                    progressBarData = "Positionnement des cases sur la carte..";
                    //mProgressDialog.setMessage(progressBarData);

                    // populates the message
                    msg = traitementMesureHandler.obtainMessage(MSG_IND,
                            (Object) progressBarData);

                    // sends the message to our handler
                    traitementMesureHandler.sendMessage(msg);

                    status = Traitement.lectureDataTemp1();

                    if (Accueil.ErrorStatus.NO_ERROR != status) {
                        Log.e("erreur1_tM", "error while parsing the file status:" + status);

                        // error management, creates an error message
                        msg = traitementMesureHandler.obtainMessage(MSG_ERR,
                                "error while parsing the file status:" + status);
                        // sends the message to our handler
                        traitementMesureHandler.sendMessage(msg);
                    } else {
                        progressBarData = "Recherche des moyennes maximales...";

                        // populates the message
                        msg = traitementMesureHandler.obtainMessage(MSG_IND,
                                (Object) progressBarData);

                        // sends the message to our handler
                        traitementMesureHandler.sendMessage(msg);

                        status = Traitement.rechercheMoyMax();

                        if (Accueil.ErrorStatus.NO_ERROR != status) {
                            Log.e("erreur2_tM", "error while computing the path status:"
                                    + status);
                            // error management,creates an error message
                            msg = traitementMesureHandler.obtainMessage(MSG_ERR,
                                    "error while computing the path status:"
                                            + status);
                            // sends the message to our handler
                            traitementMesureHandler.sendMessage(msg);
                        } else {
                            //progressBarData="Succès";
                            msg = traitementMesureHandler.obtainMessage(MSG_CNF,
                                    (Object) progressBarData);
                            // sends the message to our handler
                            traitementMesureHandler.sendMessage(msg);

                        }
                    }
                }
            }
        })).start();
    }

    final Handler traitementMesureHandler = new Handler() {
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
                    Intent intent2 = new Intent(Accueil.this, Carte.class);
                    startActivity(intent2);
                    break;
                default: // should never happen
                    break;
            }
        }
    };


    public static void makeToast(final int nbLigne, final double nombreLigne){
        final double pourcentage = (nbLigne / nombreLigne)*100;
        final DecimalFormat df = new DecimalFormat ( ) ;
        df.setMaximumFractionDigits ( 2 ) ; //arrondi à 2 chiffres apres la virgules
        final DecimalFormat df2 = new DecimalFormat ( ) ;
        df2.setMaximumFractionDigits ( 0 ) ; //arrondi à 0 chiffres apres la virgules
        final int duration = Toast.LENGTH_SHORT;

        //  Nécessaire à la création du toast indiquant l'état de l'avancement en pourcentage
        if (Looper.myLooper() == null)
        {
            Looper.prepare();
        }
        Handler makeToastHandler = new Handler(Looper.getMainLooper());

        makeToastHandler.post(new Runnable() {
            @Override
            public void run() {
                if(toast!=null){
                    toast.cancel();
                }
                toast = Toast.makeText(contextActif, nbLigne +" lignes traitées sur "+ df2.format(nombreLigne).toString() + " soit "+df.format(pourcentage).toString()+" %", duration);
                toast.show();

            };
        });

    }
    @Override
    public void onBackPressed (){
        // On ne fait rien
    }
}



