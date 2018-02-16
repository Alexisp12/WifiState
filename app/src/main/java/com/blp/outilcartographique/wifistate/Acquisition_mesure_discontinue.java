//package com.polytech.ctv.outilcartographique.wifistate;
//
///**
// * Created by Alexis on 19/05/2017.
// */
//
//        import android.Manifest;
//        import android.app.Activity;
//        import android.app.AlertDialog;
//        import android.app.ProgressDialog;
//        import android.content.Context;
//        import android.content.DialogInterface;
//        import android.content.Intent;
//        import android.content.IntentFilter;
//        import android.content.SharedPreferences;
//        import android.content.pm.PackageManager;
//        import android.net.wifi.WifiManager;
//        import android.os.Bundle;
//        import android.os.Environment;
//        import android.os.Handler;
//        import android.os.Message;
//        import android.support.v4.app.ActivityCompat;
//        import android.support.v4.content.ContextCompat;
//        import android.util.Log;
//        import android.view.LayoutInflater;
//        import android.view.View;
//        import android.widget.Button;
//        import android.widget.EditText;
//        import android.widget.ListView;
//        import android.widget.ProgressBar;
//        import android.widget.SeekBar;
//        import android.widget.TextView;
//        import android.widget.Toast;
//
//        import java.io.File;
//        import java.util.ArrayList;
//        import java.util.List;
//
//        import static com.polytech.ctv.outilcartographique.wifistate.Accueil.continuercollectbool;
//        import static com.polytech.ctv.outilcartographique.wifistate.Accueil.nouvelleCampagneBool;
//        import static com.polytech.ctv.outilcartographique.wifistate.Acquisition_mesure_continue.mesureContinueOn;
//
//public class Acquisition_mesure_discontinue extends Activity {
//    // Progression des mesures
//    protected ProgressDialog mProgressDialog;
//    private Context mContext;
//    private Accueil.ErrorStatus status;
//
//    enum ErrorStatus {
//        NO_ERROR, ERROR_1, ERROR_2
//    };
//    public static final int MSG_ERR = 0;
//    public static final int MSG_CNF = 1;
//    public static final int MSG_IND = 2;
//    public static final String TAG = "ProgressBarActivity";
//    final private int REQUEST_CODE_ASK_PERMISSIONS = 124;
//    private SeekBar.OnSeekBarChangeListener SeekBarListener;
//    private String[] files;
//    private Button boutonRechercher;
//    private Button NewData;
//    private Button Carte;
//    private Button boutonParametre;
//    private ListView listeViewWifi;
//    private List<WifiItem> listeWifiItem;
//    private TextView NbM;
//    private TextView previsualisationView;
//    private TextView NbEchantillon;
//    private TextView EchantillonView;
//    private TextView nomCampagne;
//    private TextView nomFichier;
//    public static TextView indicateurEtat;
//    private LayoutInflater alertdialogCollect;
//    public static ProgressBar progress;
//    public static int progression;
//    private WifiAdapter wifiAdapter;
//    private WifiManager wifiManager;
//    private WifiBroadcastReceiver broadcastReceiver;
//    public GPSTracker gps;
//    public static String SETS; // Nom de la sauvegarde des préférences
//    public static String nogps = "Désolé une erreur est survenue, veuillez activer la localisation";
//    public static String nomCampagneString;
//    public static String nomFichierString;
//    public static String ok = "Mesure enregistrée";
//    public static String echantillonnage ="Échantillonnage...";
//    public static String txt = ".txt"; // Lié à la concaténation
//    private String vide = "";
//    private String previsualisation="Prévisualisation :";
//    private String nowifimanager = "Problème avec WifiManager";
//    private String echantillon ="Echantillons recueillis : ";
//    private String noname = "SansNom";
//    private String wait = "Mesure en cours...";
//    private String mesure = "Mesure en cours...";
//    private String ready = "WifiState est prêt pour une nouvelle mesure";
//    private String nouvelleCollecte = "Nouvelle collecte, prêt à mesurer.";
//    private String nowifi ="Désolé une erreur est survenue, veuillez activer la WIFI";
//    final String NUMEROMESURE = "NumMesure";
//    final String NUMEROCOLLECTE = "NumCollect";
//    final String NOMBREECHANTILLONTOTAL ="nombreEchantillonTotal";
//    final String NOMCAMPAGNESTRING ="nomCampagneString";
//    final String NOMFICHIERSTRING ="nomFichierString";
//    public static boolean done; // Indication que la recherche a été réalisé
//    private boolean ouverture=true;
//    private boolean done2=true;
//    public static boolean renameOk=true;
//    no
//    public static boolean appuiBouttonMesure=false;
//    public static int pas=30; // +pas à la barprogress
//    public static int nombreEchantillonTotal;
//    public static int nombreEchantillon=0;
//    public static int NbMesures=1; // Compteur de mesures
//    public static int NData=1;// Nouvelles données
//    public static int nombreEchantillonTotalDefaut=0; // 1 collecte
//    private File repertoire;
//
//    /* Gestion barre de progression */
//
//    private Runnable mMyRunnable = new Runnable()
//    {
//        @Override
//        public void run()
//        {
//            progression=progression+pas;
//            progress.setProgress(progression);
//            Handler myHandler2 = new Handler();
//            myHandler2.postDelayed(mMyRunnable2, 900);
//        }
//    };
//    private Runnable mMyRunnable2 = new Runnable()
//    {
//        @Override
//        public void run()
//        {
//            progression=progression+pas;
//            progress.setProgress(progression);
//            Handler myHandler3 = new Handler();
//            myHandler3.postDelayed(mMyRunnable3, 900);
//        }
//    };
//    private Runnable mMyRunnable3 = new Runnable()
//    {
//        @Override
//        public void run()
//        {
//            progression=progression+pas;
//            progress.setProgress(progression);
//        }
//    };
//
//    /* Temporisation entre chaque échantillonnage */
//    private Runnable handlerTempoRunnable = new Runnable()
//    {
//        @Override
//        public void run()
//        {
//            if(nombreEchantillon!=nombreEchantillonTotal) {
//                if (done) {
//                    appuiBoutonMesure();
//                    Handler tempoHandler = new Handler();
//                    tempoHandler.postDelayed(handlerTempoRunnable, 7000);
//                    nombreEchantillon++;
//                    NbEchantillon.setText(Integer.toString(nombreEchantillon));
//                } else {
//                    Handler tempoHandler = new Handler();
//                    tempoHandler.postDelayed(handlerTempoRunnable, 7000);
//                }
//            } else {
//                // Sauvegarde du nombre de mesure dans les préférences
//                nombreEchantillon++;
//                NbEchantillon.setText(Integer.toString(nombreEchantillon));
//                indicateurEtat.setText(echantillonnage);
//                NbMesures++;
//                nombreEchantillon=0;
//                Handler tempoFinHandler = new Handler();
//                tempoFinHandler.postDelayed(handlerTempoFinRunnable,700);
//                SharedPreferences settings = getSharedPreferences(SETS, 0);
//                SharedPreferences.Editor editor = settings.edit();
//                editor.putInt("NbMesures", NbMesures);
//                editor.apply();
//            }
//        }
//    };
//    /* Temporisation à la fin du dernier échantillonnage */
//    private Runnable handlerTempoFinRunnable = new Runnable()
//    {
//        @Override
//        public void run()
//        {
//            indicateurEtat.setText(ready);
//            done2=true;
//            if(nombreEchantillon!=(nombreEchantillonTotal+1)){
//                progress.setVisibility(View.GONE);
//                previsualisationView.setVisibility(View.VISIBLE);
//                listeViewWifi.setVisibility(View.VISIBLE);
//            }
//        }
//    };
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.acquisition_mesure_discontinue);
//
//        /* Gestion permissions*/
//        int hasWriteLocationPermission = ContextCompat.checkSelfPermission(Acquisition_mesure_discontinue.this,
//                Manifest.permission.ACCESS_FINE_LOCATION);
//        if (hasWriteLocationPermission != PackageManager.PERMISSION_GRANTED) {
//            if (!ActivityCompat.shouldShowRequestPermissionRationale(Acquisition_mesure_discontinue.this,
//                    Manifest.permission.ACCESS_FINE_LOCATION)) {
//                showMessageOKCancel("You need to allow access to Localisation",
//                        new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                ActivityCompat.requestPermissions(Acquisition_mesure_discontinue.this,
//                                        new String[] {Manifest.permission.ACCESS_FINE_LOCATION},
//                                        REQUEST_CODE_ASK_PERMISSIONS);
//                            }
//                        });
//                return;
//            }
//            ActivityCompat.requestPermissions(Acquisition_mesure_discontinue.this,
//                    new String[] {Manifest.permission.ACCESS_FINE_LOCATION},
//                    REQUEST_CODE_ASK_PERMISSIONS);
//            return;
//        }
//
//        /* Affection des View */
//        /*Bouton */
//        boutonRechercher = (Button) findViewById(R.id.buttonRefresh);
//        //NewData = (Button) findViewById(R.id.NewData);
//        boutonParametre = (Button) findViewById(R.id.parametre);
//        Carte = (Button) findViewById(R.id.Carte);
//        /*TextView*/
//        NbM = (TextView) findViewById(R.id.NbM);
//        previsualisationView = (TextView) findViewById(R.id.Previsualisation);
//        indicateurEtat = (TextView) findViewById(R.id.Wait);
//        NbEchantillon = (TextView) findViewById(R.id.NbEchantillon);
//        EchantillonView = (TextView) findViewById(R.id.Echantillon);
//        nomCampagne = (TextView) findViewById(R.id.nomCampagne);
//        nomFichier = (TextView) findViewById(R.id.nomFichier);
//
//        /*Liste*/
//        listeViewWifi = (ListView) findViewById(R.id.listViewWifi);
//        /*Alerte dialogue*/
//        alertdialogCollect = LayoutInflater.from(this);
//        /*Barre de progression*/
//        progress=(ProgressBar) findViewById(R.id.progressbar);
//
//        /* Initilisation barre de progression*/
//        progression=0;
//        progress.setProgress(progression);
//        progress.setVisibility(View.GONE);
//
//
//        /* Récupération de l'état précédent (numéro de mesure, numéro de collecte, nom de collecte, nombreEchantillonTotal)*/
//        SharedPreferences settings = getSharedPreferences(SETS, 0);
//        NbMesures = settings.getInt("NbMesures", NbMesures);
//        NData = settings.getInt("NData", NData);
//        nombreEchantillonTotal = settings.getInt("nombreEchantillonTotal",nombreEchantillonTotal);
//        nomCampagneString = settings.getString("nomCampagneString",nomCampagneString);
//        nomFichierString = settings.getString("nomFichier", nomFichierString);
//
//        /* Initialisation message texte */
//        indicateurEtat.setText(ready);
//        EchantillonView.setText(vide);
//        previsualisationView.setText(vide);
//        NbEchantillon.setText(vide);
//        NbM.setText(String.valueOf(NbMesures)); // Affichage Nombre mesure
//        nomCampagne.setText(nomCampagneString);
//        nomFichier.setText(nomFichierString);
//
//
//        if(continuercollectbool){
//            nomCampagne.setText(nomCampagneString);
//            nomFichier.setText(nomFichierString);
//            continuercollectbool=false;
//        }
//
//
//        if(nouvelleCampagneBool){
//            // Actualisation messages textes
//            NbM.setText(String.valueOf(NbMesures)); // Affichage Nombre mesure
//            indicateurEtat.setText(vide);
//            EchantillonView.setText(vide);
//            previsualisationView.setText(vide);
//            NbEchantillon.setText(vide);
//            nomCampagne.setText(nomCampagneString);
//            nomFichier.setText(nomFichierString);
//
//            nombreEchantillon=0;
//            nomCampagne.setText(nomCampagneString);
//
//            // Actualisation barre de progression
//            progress.setProgress(0);
//            progress.setVisibility(View.GONE);
//            listeViewWifi.setVisibility(View.GONE);
//            indicateurEtat.setText(nouvelleCollecte);
//            nouvelleCampagneBool=false;
//            // Incorparation des nouvelles données dans les préférences
//            SharedPreferences.Editor editor = settings.edit();
//            //editor.putInt("NData", NData);
//            editor.putInt("NbMesures", NbMesures);
//            editor.putInt("nombreEchantillonTotal", nombreEchantillonTotal);
//            editor.putString("nomCampagneString", nomCampagneString);
//            editor.putString("nomFichier", nomFichierString);
//            editor.commit();
//        }
//
//        /* Génération du gps */
//        gps = new GPSTracker(this);
//
//        /* Initialisation WifiManger*/
//        // On récupère le service Wifi d'Android
//        wifiManager = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);
//
//        // Gestion de la liste des AP Wifi
//        listeWifiItem = new ArrayList<WifiItem>();
//        wifiAdapter = new WifiAdapter(this, listeWifiItem);
//        listeViewWifi.setAdapter(wifiAdapter);
//
//        // Création du broadcast Receiver
//        broadcastReceiver = new WifiBroadcastReceiver();
//
//        // On attache le receiver au scan result
//        registerReceiver(broadcastReceiver, new IntentFilter(
//                WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
//
//        // Lecture du nom des fichiers textes
//        repertoire= new File(Environment.getExternalStorageDirectory() + File.separator + "WifiStateData");
//
//        /* S'il n'y a aucune collecte de créé*/
//        /*
//        if(!repertoire.exists()){
//
//            final View alertDialogViewNouvelleCollecte = alertdialogCollect.inflate(R.layout.alertdialog_nouvelle_collecte, null);
//
//            AlertDialog.Builder alertdial = new AlertDialog.Builder(Acquisition_mesure_discontinue.this);
//            alertdial.setView(alertDialogViewNouvelleCollecte);
//            alertdial.setTitle("Nouvelle collecte");
//            final EditText nomCampagneEdit = (EditText)alertDialogViewNouvelleCollecte.findViewById(R.id.EditText1);
//            final EditText nombreEchantillon = (EditText)alertDialogViewNouvelleCollecte.findViewById(R.id.EditText2);
//            final SeekBar seekBar= (SeekBar) alertDialogViewNouvelleCollecte.findViewById(R.id.seekBar);
//            seekBar.setProgress(1);
//
//            SeekBar.OnSeekBarChangeListener SeekBarListener =
//                    new SeekBar.OnSeekBarChangeListener() {
//                        @Override
//                        public void onProgressChanged (SeekBar seekBar, int progress, boolean fromUser) {
//                            nombreEchantillon.setText(Integer.toString(progress));
//                        }
//
//                        @Override
//                        public void onStartTrackingTouch(SeekBar seekBar) {
//
//                        }
//
//                        @Override
//                        public void onStopTrackingTouch(SeekBar seekBar) {
//
//                        }
//                    };
//
//            seekBar.setOnSeekBarChangeListener(SeekBarListener);
//            alertdial.setPositiveButton("Enregistrer", new DialogInterface.OnClickListener() {
//                public void onClick(DialogInterface dialog, int which) {
//                    if(nomCampagneEdit.getText().length() == 0) {
//                        nomCampagneString = noname;
//                    } else {
//                        nomCampagneString = nomCampagneEdit.getText().toString();
//                    }
//                    if(nombreEchantillon.getText().length()==0){
//                        nombreEchantillonTotal = nombreEchantillonTotalDefaut;
//                    } else {
//                        nombreEchantillonTotal = (Integer.parseInt(nombreEchantillon.getText().toString())) - 1;
//                    }
//                    nomCampagne.setText(nomCampagneString);
//                    nomFichierString=nomCampagneString+txt;
//                    nomFichier.setText(nomFichierString);
//                    // Récupération des données
//                    SharedPreferences settings = getSharedPreferences(SETS, 0);
//                    // Incorparation des nouvelles données dans les préférences
//                             */
//        /*
//                    SharedPreferences.Editor editor = settings.edit();
//                    editor.putInt("nombreEchantillonTotal", nombreEchantillonTotal);
//                    editor.putString("nomCampagneString", nomCampagneString);
//                    editor.putInt("NData", NData);
//                    editor.putInt("NbMesures", NbMesures);
//                    editor.putString("nomFichier",nomFichierString);
//                    editor.commit();
//                }
//            });
//            alertdial.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
//                public void onClick(DialogInterface dialog, int which) {
//                    // On ne fais rien si l'utilisateur clique sur annuler
//                }
//            });
//            alertdial.setIcon(android.R.drawable.ic_dialog_info);
//            alertdial.show();
//
//        }
//        */
//
//        /*Bouton paramètre*/
//        boutonParametre.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View parametre) {
//                if ((done || ouverture) && done2) {
//                    Intent intent_parametre = new Intent(Acquisition_mesure_discontinue.this, Parametre_acquisition.class);
//                    intent_parametre.putExtra(NOMBREECHANTILLONTOTAL, nombreEchantillonTotal);
//                    intent_parametre.putExtra(NOMCAMPAGNESTRING, nomCampagneString);
//                    intent_parametre.putExtra(NOMFICHIERSTRING,nomFichierString);
//                    startActivity(intent_parametre);
//                }
//            }
//        });
//
//        /* Bouton nouvelle mesure*/
//        boutonRechercher.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View NouvelleMesure) {
//                if ((done || ouverture) && done2) {
//                    NbEchantillon.setText(Integer.toString(nombreEchantillon));
//                    done2=false;
//                    appuiBoutonMesure();
//                    Handler tempoHandler = new Handler();
//                    tempoHandler.postDelayed(handlerTempoRunnable, 7000); // Supérieur à la durée d'un échantillonnage
//                }
//            }
//        });
//
//        /*Bouton nouvelle collecte*/
// /*       NewData.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View Newdata) {
//                if ((done || ouverture) && done2) {
//                    appuiBoutonCollecte();
//                    nombreEchantillon=0;
//                    nomCampagne.setText(nomCampagneString);
//                }
//            }
//        });
//*/
//        /* Bouton Afficher la carte*/
//        Carte.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View Carte) {
//                if(done || ouverture) {
//                    appuiBoutonCarte();
//                }
//            }
//        });
//    }
//
//    public void appuiBoutonCollecte(){
//        AlertDialog.Builder alertdial = new AlertDialog.Builder(Acquisition_mesure_discontinue.this);
//        alertdial.setTitle("Nouvelle collecte");
//        alertdial.setMessage("Êtes-vous sûr de vouloir recommencer une série de mesures ?");
//        alertdial.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
//            // Configuration nouvelle collecte
//            final View alertDialogViewNouvelleCollecte = alertdialogCollect.inflate(R.layout.alertdialog_nouvelle_collecte, null);
//            public void onClick(DialogInterface dialog, int which) {
//                AlertDialog.Builder alertdial = new AlertDialog.Builder(Acquisition_mesure_discontinue.this);
//                alertdial.setView(alertDialogViewNouvelleCollecte);
//                alertdial.setTitle("Nouvelle collecte");
//                final EditText nomCampagneEdit = (EditText)alertDialogViewNouvelleCollecte.findViewById(R.id.EditText1);
//                final EditText nombreEchantillon = (EditText)alertDialogViewNouvelleCollecte.findViewById(R.id.EditText2);
//                final SeekBar seekBar= (SeekBar) alertDialogViewNouvelleCollecte.findViewById(R.id.seekBar);
//                seekBar.setProgress(1);
//
//                SeekBar.OnSeekBarChangeListener SeekBarListener =
//                        new SeekBar.OnSeekBarChangeListener() {
//                            @Override
//                            public void onProgressChanged (SeekBar seekBar, int progress, boolean fromUser) {
//                                nombreEchantillon.setText(Integer.toString(progress));
//                            }
//
//                            @Override
//                            public void onStartTrackingTouch(SeekBar seekBar) {
//
//                            }
//
//                            @Override
//                            public void onStopTrackingTouch(SeekBar seekBar) {
//
//                            }
//                        };
//
//                seekBar.setOnSeekBarChangeListener(SeekBarListener);
//                // Enregistrement de la configuration
//                alertdial.setPositiveButton("Enregistrer", new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int which) {
//                        repertoire= new File(Environment.getExternalStorageDirectory() + File.separator + "WifiStateData");
//                        files = repertoire.list();
//
//                        if (nomCampagneEdit.getText().length() == 0) {
//                            for(int i =0; i<files.length;i++) {
//                                if(files[i].equals(noname+txt)){
//                                    renameOk=false;
//                                }
//                            }
//                        } else {
//                            for(int i =0; i<files.length;i++) {
//                                if(files[i].equals(nomCampagneEdit.getText().toString()+txt)){
//                                    renameOk=false;
//                                }
//                            }
//                        }
//
//
//                        if(renameOk) {
//                            if(nomCampagneEdit.getText().length() == 0){
//                                nomCampagneString=noname;
//                            } else {
//                                nomCampagneString=nomCampagneEdit.getText().toString();
//                            }
//                            if (nombreEchantillon.getText().length() == 0) {
//                                nombreEchantillonTotal = nombreEchantillonTotalDefaut;
//                            } else {
//                                nombreEchantillonTotal = (Integer.parseInt(nombreEchantillon.getText().toString())) - 1;
//                            }
//
//                            /* Récupération des données */
//                            SharedPreferences settings = getSharedPreferences(SETS, 0);
//                            NData = settings.getInt("NData", NData);
//
//                            /* RAZ du nombre de mesure */
//                            NbMesures = 1;
//
//                            /* Incrémentation du numéro de collecte */
//                            NData++;
//
//                            /* Nom du fichier de data*/
//                            nomFichierString = nomCampagneString + txt;
//
//                            /* Incorparation des nouvelles données dans les préférences*/
//                            SharedPreferences.Editor editor = settings.edit();
//                            editor.putInt("NData", NData);
//                            editor.putInt("NbMesures", NbMesures);
//                            editor.putInt("nombreEchantillonTotal", nombreEchantillonTotal);
//                            editor.putString("nomCampagneString", nomCampagneString);
//                            editor.putString("nomFichier", nomFichierString);
//                            editor.commit();
//
//                            /* Actualisation messages textes*/
//                            NbM.setText(String.valueOf(NbMesures)); // Affichage Nombre mesure
//                            indicateurEtat.setText(vide);
//                            EchantillonView.setText(vide);
//                            previsualisationView.setText(vide);
//                            NbEchantillon.setText(vide);
//                            nomCampagne.setText(nomCampagneString);
//                            nomFichier.setText(nomFichierString);
//
//                            /* Actualisation barre de progression*/
//                            progress.setProgress(0);
//                            progress.setVisibility(View.GONE);
//                            listeViewWifi.setVisibility(View.GONE);
//                            indicateurEtat.setText(nouvelleCollecte);
//                        } else {
//                            renameOk=true;
//                            AlertDialog.Builder alertdial = new AlertDialog.Builder(Acquisition_mesure_discontinue.this);
//                            alertdial.setTitle("Un fichier portant ce nom existe déjà");
//                            alertdial.setMessage("Choisissez un nom de campagne qui n'a pas déjà été utilisé");
//                            alertdial.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
//                                public void onClick(DialogInterface dialog, int which) {
//
//                                }
//                            });
//                            alertdial.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
//                                public void onClick(DialogInterface dialog, int which) {
//
//                                }
//                            });
//                            alertdial.setIcon(android.R.drawable.ic_dialog_alert);
//                            alertdial.show();
//                        }
//                    }
//                });
//                alertdial.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int which) {
//                        // On ne fais rien si l'utilisateur clique sur annuler
//                    }
//                });
//                alertdial.setIcon(android.R.drawable.ic_dialog_info);
//                alertdial.show();
//            }
//        });
//        alertdial.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface dialog, int which) {
//                // On ne fais rien si l'utilisateur clique sur annuler
//            }
//        });
//        alertdial.setIcon(android.R.drawable.ic_dialog_info);
//        alertdial.show();
//    }
//
//    public void appuiBoutonMesure(){
//        /* Boolean permettant de bloquer certaines fonctionnalité pendant la mesure*/
//        appuiBouttonMesure=true;
//        mesureContinueOn=false;
//        /*Actualisation des messages indicateurs*/
//        previsualisationView.setText(previsualisation);
//        EchantillonView.setText(echantillon);
//        listeViewWifi.setVisibility(View.GONE);
//        progress.setVisibility(View.VISIBLE);
//        previsualisationView.setVisibility(View.GONE);
//        /*Récupération données GPS*/
//        gps.onLocationChanged(gps.getLocation());
//        latitude = gps.getLatitude();
//        longitude = gps.getLongitude();
//        // A but de comparaison
//        String lat = Double.toString(latitude);
//        String longi = Double.toString(latitude);
//
//
//        if (wifiManager.isWifiEnabled()) {
//            if (gps != null && !lat.equals("0.0") && !longi.equals("0.0")) {
//                /* Gestion barre de progression*/
//                if(done || ouverture) {
//                    ouverture = false;
//                    progress.setProgress(10);
//                    //progression = progression + pas;
//                    Handler myHandler = new Handler();
//                    myHandler.postDelayed(mMyRunnable, 900);
//                    done=false;
//                }
//                wifiManager.startScan();
//                SharedPreferences settings = getSharedPreferences(SETS, 0); // Update de l'état courant
//                NbMesures = settings.getInt("NbMesures", NbMesures);
//                nombreEchantillonTotal = settings.getInt("nombreEchantillonTotal",nombreEchantillonTotal);
//                NbM.setText(String.valueOf(NbMesures)); // Affichage Nombre mesure
//                if (!done && lat != "0.0" && longi != "0.0") {
//                    indicateurEtat.setText(mesure);
//                } else {
//                    indicateurEtat.setText(wait);
//                }
//
//            } else {
//                indicateurEtat.setText(wait);
//                listeViewWifi.setVisibility(View.GONE);
//                progress.setVisibility(View.GONE);
//                EchantillonView.setText(vide);
//                previsualisationView.setText(vide);
//                NbEchantillon.setText(vide);
//                appuiBouttonMesure=false;
//                AlertDialog.Builder alertdial = new AlertDialog.Builder(Acquisition_mesure_discontinue.this);
//                alertdial.setTitle("Localisation introuvable");
//                alertdial.setMessage("Activez la localisation avant d'utiliser l'application, redémarrer-là si nécessaire");
//                alertdial.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int which) {
//                        indicateurEtat.setText(nogps);
//                    }
//                });
//                alertdial.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int which) {
//                        indicateurEtat.setText(nogps);
//                    }
//                });
//                alertdial.setIcon(android.R.drawable.ic_dialog_alert);
//                alertdial.show();
//
//            }
//        } else {
//            listeViewWifi.setVisibility(View.GONE);
//            progress.setVisibility(View.GONE);
//            EchantillonView.setText(vide);
//            previsualisationView.setText(vide);
//            NbEchantillon.setText(vide);
//            appuiBouttonMesure=false;
//            indicateurEtat.setText(wait);
//            AlertDialog.Builder alertdial = new AlertDialog.Builder(Acquisition_mesure_discontinue.this);
//            alertdial.setTitle("Données Wifi introuvable");
//            alertdial.setMessage("Please try to enable Wifi in your settings, restart the application if necessary.");
//            alertdial.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
//                public void onClick(DialogInterface dialog, int which) {
//                    indicateurEtat.setText(nowifi);
//                }
//            });
//            alertdial.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
//                public void onClick(DialogInterface dialog, int which) {
//                    indicateurEtat.setText(nowifi);
//                }
//            });
//            alertdial.setIcon(android.R.drawable.ic_dialog_alert);
//            alertdial.show();
//        }
//    }
//    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
//        new AlertDialog.Builder(Acquisition_mesure_discontinue.this)
//                .setMessage(message)
//                .setPositiveButton("OK", okListener)
//                .setNegativeButton("Cancel", null)
//                .create()
//                .show();
//    }
//
//    public void appuiBoutonCarte(){
//        AlertDialog.Builder alertdial = new AlertDialog.Builder(Acquisition_mesure_discontinue.this);
//        alertdial.setTitle("Afficher la carte");
//        alertdial.setMessage("Afficher la carte nécessite d'avoir terminé la collecte de mesures, voulez-vous continuer ?");
//        // L'utilisateur est OK
//        alertdial.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface dialog, int which) {
//                indicateurEtat.setText(ready);
//                progress.setProgress(0);
//                listeViewWifi.setVisibility(View.GONE);
//                progress.setVisibility(View.GONE);
//                EchantillonView.setText(vide);
//                previsualisationView.setText(vide);
//                NbEchantillon.setText(vide);
//                nombreEchantillon=0;
//                // CARTE
//                traitementMesures();
////                Intent intent = new Intent(Acquisition_mesure_discontinue.this, Seuils.class);
////                intent.putExtra(NUMEROMESURE, NbMesures);
////                intent.putExtra(NUMEROCOLLECTE, NData);
////                startActivity(intent);
//            }
//        });
//        alertdial.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface dialog, int which) {
//
//            }
//        });
//        alertdial.setIcon(android.R.drawable.ic_dialog_info);
//        alertdial.show();
//    }
//
//    public void traitementMesures(){
//        mProgressDialog = ProgressDialog.show(this, "Patientez...",
//                "Long operation starts...", true);
//
//        // useful code, variables declarations...
//        new Thread((new Runnable() {
//            @Override
//            public void run() {
//                Message msg = null;
//                String progressBarData = "Lecture des données recueillies...";
//
//                // populates the message
//                msg = mHandler.obtainMessage(MSG_IND, (Object) progressBarData);
//
//                // sends the message to our handler
//                mHandler.sendMessage(msg);
//
//                // starts the first long operation
//                status =Traitement.lectureData(nomFichierString);
//
//                if (Accueil.ErrorStatus.NO_ERROR != status) {
//                    Log.e("erreur1_tM", "error while parsing the file status:" + status);
//
//                    // error management, creates an error message
//                    msg = mHandler.obtainMessage(MSG_ERR,
//                            "error while parsing the file status:" + status);
//                    // sends the message to our handler
//                    mHandler.sendMessage(msg);
//                } else {
//                    //progressBarData="Succès";
//                    msg = mHandler.obtainMessage(MSG_CNF,
//                            (Object) progressBarData);
//                    // sends the message to our handler
//                    mHandler.sendMessage(msg);
//
//                    Intent intent = new Intent(Acquisition_mesure_discontinue.this, Carte.class);
//                    intent.putExtra(NUMEROMESURE, NbMesures);
//                    intent.putExtra(NUMEROCOLLECTE, NData);
//                    startActivity(intent);
//                }
//            }
//        })).start();
//    }
//
//    final Handler mHandler = new Handler() {
//        public void handleMessage(Message msg) {
//            String text2display = null;
//            switch (msg.what) {
//                case MSG_IND:
//                    if (mProgressDialog.isShowing()) {
//                        mProgressDialog.setMessage(((String) msg.obj));
//                    }
//                    break;
//                case MSG_ERR:
//                    text2display = (String) msg.obj;
//                    Toast.makeText(mContext, "Error: " + text2display,
//                            Toast.LENGTH_LONG).show();
//                    if (mProgressDialog.isShowing()) {
//                        mProgressDialog.dismiss();
//                    }
//                    break;
//                case MSG_CNF:
//                    //text2display = (String) msg.obj;
//                    //Toast.makeText(mContext, "Info: " + text2display,
//                    //        Toast.LENGTH_LONG).show();
//                    if (mProgressDialog.isShowing()) {
//                        mProgressDialog.dismiss();
//                    }
//
//                    break;
//                default: // should never happen
//                    break;
//            }
//        }
//    };
//
//    // On arrête le receiver quand on met en pause l'application
//    @Override
//    protected void onPause() {
//        unregisterReceiver(broadcastReceiver);
//        super.onPause();
//    }
//
//    // On remet en rourte le receiver quand on reviens sur l'application
//    @Override
//    protected void onResume() {
//        registerReceiver(broadcastReceiver, new IntentFilter(
//                WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
//        super.onResume();
//    }
//
//    public WifiManager getCurrentWifiManager() {
//        return wifiManager;
//    }
//
//    public WifiAdapter getWifiAdapter() {
//        return wifiAdapter;
//    }
//
//    public List<WifiItem> getListeWifiItem() {
//        return listeWifiItem;
//    }
//
//}
//
