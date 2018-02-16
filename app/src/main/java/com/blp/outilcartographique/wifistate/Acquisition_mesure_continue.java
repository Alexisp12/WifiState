package com.blp.outilcartographique.wifistate;

/**
 * Created by Alexis on 19/05/2017.
 */

        import android.Manifest;
        import android.annotation.SuppressLint;
        import android.app.Activity;
        import android.app.AlertDialog;
        import android.app.ProgressDialog;
        import android.content.Context;
        import android.content.DialogInterface;
        import android.content.Intent;
        import android.content.IntentFilter;
        import android.content.SharedPreferences;
        import android.content.pm.PackageManager;
        import android.net.wifi.WifiManager;
        import android.os.Bundle;
        import android.os.Environment;
        import android.os.Handler;
        import android.os.Message;
        import android.support.v4.app.ActivityCompat;
        import android.support.v4.content.ContextCompat;
        import android.util.Log;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.widget.AdapterView;
        import android.widget.ArrayAdapter;
        import android.widget.Button;
        import android.widget.EditText;
        import android.widget.ImageButton;
        import android.widget.ListView;
        import android.widget.ProgressBar;
        import android.widget.SeekBar;
        import android.widget.Spinner;
        import android.widget.TextView;
        import android.widget.Toast;

        import java.io.File;
        import java.util.ArrayList;
        import java.util.List;

        import static com.blp.outilcartographique.wifistate.Accueil.contextActif;
        import static com.blp.outilcartographique.wifistate.Accueil.continuercollectbool;
        import static com.blp.outilcartographique.wifistate.Accueil.nouvelleCampagneBool;
        import static com.blp.outilcartographique.wifistate.Accueil.Seuil5;
        import static com.blp.outilcartographique.wifistate.Accueil.choixSSID;
        import static com.blp.outilcartographique.wifistate.Traitement.LENGTH_MAX_VALUE;

/**
 * Created by Alexis on 21/02/2017.
 */

public class Acquisition_mesure_continue extends Activity {
    private EditText EditSeuil5;
    private int ValSeuil5= 5;
    // Progression des mesures
    protected ProgressDialog mProgressDialog;
    private Accueil.ErrorStatus status;
    public static int nombreEchantillon=0;
    public static int NbMesures=1; // Compteur de mesures
    public static int nombreEchantillonTotal;
    public static final int MSG_ERR = 0;
    public static final int MSG_CNF = 1;
    public static final int MSG_IND = 2;
    final private int REQUEST_CODE_ASK_PERMISSIONS = 124;
    private SeekBar.OnSeekBarChangeListener SeekBarListener;
    private String[] files;
    private ImageButton boutonRechercher;
    private ImageButton boutonStop;
    private ImageButton Carte;
    private ImageButton boutonParametre;
    private ListView listeViewWifi;
    private List<WifiItem> listeWifiItem;
    private TextView NbM;
    private TextView previsualisationView;
    private TextView NbEchantillon;
    private TextView EchantillonView;
    private TextView nomCampagne;
    private TextView nomFichier;
    private TextView messageGPS;
    private TextView distanceParcourue;
    private double distLong;
    private double distLat;
    private double distLongLat;
    private double exLongitude;
    private double exLatitude;
    private double distParcourue;
    public static double latitude;
    public static double longitude;
    public static TextView indicateurEtat;
    private LayoutInflater alertdialogCollect;
    public static ProgressBar progress;
    private WifiAdapter wifiAdapter;
    private WifiManager wifiManager;
    private WifiBroadcastReceiver broadcastReceiver;
    public GPSTracker gps;
    public static String SETS; // Nom de la sauvegarde des préférences
    public static String nomFichierString;
    public static String nomCampagneString;
    private String vide = "";
    public static String echantillonnage ="Échantillonnage...";
    private String previsualisation="Prévisualisation :";
    private String echantillon ="Echantillons recueillis : ";
    private String wait = "Une acquisition est en cours";
    private String mesure = "Une acquisition est en cours";
    private String ready = "WifiState est prêt pour une nouvelle acquisition";
    private String nouvelleCollecte = "Nouvelle campagne, prêt à mesurer.";
    private String nowifi ="Désolé une erreur est survenue, veuillez activer la WIFI";
    public static String nogps = "Désolé une erreur est survenue, veuillez activer la localisation";
    final String NUMEROMESURE = "NumMesure";
    final String NUMEROCOLLECTE = "NumCollect";
    final String NOMCAMPAGNESTRING ="nomCampagneString";
    final String NOMFICHIERSTRING ="nomFichierString";
    private boolean ouverture=true;
    private boolean done2=true;
    public static boolean done; // Indication que la recherche a été réalisé
    public static boolean appuiBouttonMesure=false;
    public static boolean appuiBoutonStopBool=false;
    public static boolean mesureContinueOn=false;
    public static boolean coordGPSUpdated=false;
    private static boolean actualisationEnCours=false;
    public static boolean renameOk=true;

    // Attente de l'actualisation des coordonnées GPS lors de l'appui sur le bouton start suite à un "stop"
    private Runnable mMyRunnable = new Runnable()
    {
        @Override
        public void run()
        {
            actualisationEnCours=true;
            gps.onLocationChanged(gps.getLocation());

            Log.d("getLongitude",Double.toString(gps.getLongitude()));
            Log.d("getLatitude",Double.toString(gps.getLatitude()));

            if(exLatitude!= gps.getLatitude() && exLongitude!=gps.getLongitude()){
                coordGPSUpdated=true;
                actualisationEnCours=false;
                nouvelleMesure();
            } else {
                Handler myHandler = new Handler();
                myHandler.postDelayed(mMyRunnable, 10000);
                int duration = Toast.LENGTH_LONG;
                Toast toast = Toast.makeText(contextActif,"Patientez quelques minutes. Actualisation GPS.", duration);
                toast.show();
            }

        }
    };

    /* Temporisation entre chaque échantillonnage */
    private Runnable handlerTempoRunnable = new Runnable()
    {
        @Override
        public void run()
        {
            if(!appuiBoutonStopBool) {
                if (done) {
                    appuiBoutonMesure();
                    previsualisationView.setVisibility(View.VISIBLE);
                    listeViewWifi.setVisibility(View.VISIBLE);
                    Handler tempoHandler = new Handler();
                    tempoHandler.postDelayed(handlerTempoRunnable, 500);
                    nombreEchantillon++;
                    NbEchantillon.setText(Integer.toString(nombreEchantillon));
                } else {
                    if(appuiBouttonMesure) {
                        Handler tempoHandler = new Handler();
                        tempoHandler.postDelayed(handlerTempoRunnable, 500);
                    } else {
                        indicateurEtat.setText(ready);
                        distanceParcourue.setVisibility(View.GONE);
                        done=true;
                        done2=true;
                    }
                }
            } else {
                // Sauvegarde du nombre de mesure dans les préférences
                if(done) {
                    nombreEchantillon++;
                    NbEchantillon.setText(Integer.toString(nombreEchantillon));
                    NbMesures++;
                    nombreEchantillon = 0;
                    indicateurEtat.setText(ready);
                    done2=true;
                    progress.setVisibility(View.GONE);
                    previsualisationView.setVisibility(View.VISIBLE);
                    listeViewWifi.setVisibility(View.VISIBLE);

                    SharedPreferences settings = getSharedPreferences(SETS, 0);
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putInt("NbMesures", NbMesures);
                    editor.apply();
                } else {
                    if(appuiBouttonMesure) {
                        indicateurEtat.setText(echantillonnage);
                        Handler tempoHandler = new Handler();
                        tempoHandler.postDelayed(handlerTempoRunnable, 500);
                    } else {
                        indicateurEtat.setText(ready);
                        distanceParcourue.setVisibility(View.GONE);
                        done=true;
                        done2=true;
                    }
                }
            }
        }
    };

    @SuppressLint("WifiManagerLeak")
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acquisition_mesure_continue);

        mesureContinueOn=true;

        /* Gestion permissions*/
        int hasWriteLocationPermission = ContextCompat.checkSelfPermission(Acquisition_mesure_continue.this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        if (hasWriteLocationPermission != PackageManager.PERMISSION_GRANTED) {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(Acquisition_mesure_continue.this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                showMessageOKCancel("You need to allow access to Localisation",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ActivityCompat.requestPermissions(Acquisition_mesure_continue.this,
                                        new String[] {Manifest.permission.ACCESS_FINE_LOCATION},
                                        REQUEST_CODE_ASK_PERMISSIONS);
                            }
                        });
                return;
            }
            ActivityCompat.requestPermissions(Acquisition_mesure_continue.this,
                    new String[] {Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_CODE_ASK_PERMISSIONS);
            return;
        }

        /*AlertDialog*/
        alertdialogCollect = LayoutInflater.from(this);

        /* Affection des View */
        /*Bouton */
        boutonRechercher = (ImageButton) findViewById(R.id.buttonRefresh);
        boutonStop = (ImageButton) findViewById(R.id.buttonstop);
        //NewData = (Button) findViewById(R.id.NewData);
        boutonParametre = (ImageButton) findViewById(R.id.parametre);
        Carte = (ImageButton) findViewById(R.id.Carte);
        /*TextView*/
        NbM = (TextView) findViewById(R.id.NbM);
        previsualisationView = (TextView) findViewById(R.id.Previsualisation);
        indicateurEtat = (TextView) findViewById(R.id.Wait);
        NbEchantillon = (TextView) findViewById(R.id.NbEchantillon);
        EchantillonView = (TextView) findViewById(R.id.Echantillon);
        nomCampagne = (TextView) findViewById(R.id.nomCampagne);
        nomFichier = (TextView) findViewById(R.id.nomFichier);
        distanceParcourue = (TextView) findViewById(R.id.distanceParcourue);
        messageGPS = (TextView) findViewById(R.id.messageGPS);

        /*Liste*/
        listeViewWifi = (ListView) findViewById(R.id.listViewWifi);
        /*Alerte dialogue*/
        alertdialogCollect = LayoutInflater.from(this);
        /*Progression*/
        progress=(ProgressBar) findViewById(R.id.progressbar);
        progress.setVisibility(View.GONE);


        /* Récupération de l'état précédent (numéro de mesure, numéro de collecte, nom de collecte, nombreEchantillonTotal)*/
        SharedPreferences settings = getSharedPreferences(SETS, 0);
        NbMesures = settings.getInt("NbMesures", NbMesures);
        nomCampagneString = settings.getString("nomCampagneString",nomCampagneString);
        nomFichierString = settings.getString("nomFichier", nomFichierString);

        /* Initialisation message texte */
        indicateurEtat.setText(ready);
        EchantillonView.setText(vide);
        previsualisationView.setText(vide);
        NbEchantillon.setText(vide);
        NbM.setText(String.valueOf(NbMesures)); // Affichage Nombre mesure
        nomCampagne.setText(nomCampagneString);
        nomFichier.setText(nomFichierString);

        /* On cache le bouton stop*/
        boutonStop.setVisibility(View.GONE);


        if(continuercollectbool){
            nomCampagne.setText(nomCampagneString);
            nomFichier.setText(nomFichierString);
            continuercollectbool=false;
            messageGPS.setVisibility(View.GONE);
            distanceParcourue.setVisibility(View.GONE);
        }


        if(nouvelleCampagneBool){
            // Actualisation messages textes
            NbM.setText(String.valueOf(NbMesures)); // Affichage Nombre mesure
            indicateurEtat.setText(vide);
            EchantillonView.setText(vide);
            previsualisationView.setText(vide);
            NbEchantillon.setText(vide);
            nomCampagne.setText(nomCampagneString);
            nomFichier.setText(nomFichierString);
            messageGPS.setVisibility(View.GONE);
            distanceParcourue.setVisibility(View.GONE);
            nombreEchantillon=0;
            nomCampagne.setText(nomCampagneString);

            // Actualisation barre de progression
            progress.setVisibility(View.GONE);
            listeViewWifi.setVisibility(View.GONE);
            indicateurEtat.setText(nouvelleCollecte);
            nouvelleCampagneBool=false;
            // Incorparation des nouvelles données dans les préférences
            SharedPreferences.Editor editor = settings.edit();
            //editor.putInt("NData", NData);
            editor.putInt("NbMesures", NbMesures);
            editor.putString("nomCampagneString", nomCampagneString);
            editor.putString("nomFichier", nomFichierString);
            editor.commit();
        }

        /* Génération du gps */
        gps = new GPSTracker(this);

        /* Initialisation WifiManger*/
        // On récupère le service Wifi d'Android
        wifiManager = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);

        // Gestion de la liste des AP Wifi
        listeWifiItem = new ArrayList<WifiItem>();
        wifiAdapter = new WifiAdapter(this, listeWifiItem);
        listeViewWifi.setAdapter(wifiAdapter);

        // Création du broadcast Receiver
        broadcastReceiver = new WifiBroadcastReceiver();

        // On attache le receiver au scan result
        registerReceiver(broadcastReceiver, new IntentFilter(
                WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));

        /*Bouton paramètre*/
        boutonParametre.setOnClickListener(new View.OnClickListener() {
            public void onClick(View parametre) {
                if ((done || ouverture) && done2) {
                    Intent intent_parametre = new Intent(Acquisition_mesure_continue.this, Parametre_acquisition.class);
                    intent_parametre.putExtra(NOMCAMPAGNESTRING, nomCampagneString);
                    intent_parametre.putExtra(NOMFICHIERSTRING,nomFichierString);
                    startActivity(intent_parametre);
                }
            }
        });

        /* Bouton nouvelle mesure*/
        boutonRechercher.setOnClickListener(new View.OnClickListener() {
            public void onClick(View NouvelleMesure) {
                if(!actualisationEnCours) {
                    if (nombreEchantillon == 0) {
                        // On check l'actualisation des coordonnées GPS pour ne pas avoir les premières mesures faussées
                        if (!coordGPSUpdated) {
                            Handler myHandler = new Handler();
                            myHandler.postDelayed(mMyRunnable, 900);
                        } else {
                            nouvelleMesure();
                        }
                    } else {
                        nouvelleMesure();
                    }
                }
            }
        });

        boutonStop.setOnClickListener(new View.OnClickListener() {
            public void onClick(View NouvelleMesure) {
                boutonStop.setVisibility(View.GONE);
                boutonRechercher.setVisibility(View.VISIBLE);
                distanceParcourue.setVisibility(View.GONE);
                messageGPS.setVisibility(View.GONE);
                progress.setVisibility(View.GONE);
                appuiBoutonStopBool=true;
                coordGPSUpdated=false;
            }
        });

        /* Bouton Afficher la carte*/
        Carte.setOnClickListener(new View.OnClickListener() {
            public void onClick(View Carte) {
                if(done && done2) {
                    File mesures = new File(Environment.getExternalStorageDirectory() + File.separator + "WifiStateData", nomCampagneString + ".txt");
                    if(mesures.exists()) {
                        appuiBoutonCarte();
                    } else {
                        Context context = getApplicationContext();
                        CharSequence text = "Aucune donnée n'a été récupéré.";
                        int duration = Toast.LENGTH_LONG;
                        Toast toast = Toast.makeText(context, text, duration);
                        toast.show();
                    }
                } else {
                    if(nombreEchantillon==0) {
                        Context context = getApplicationContext();
                        CharSequence text = "Vous devez d'abord effectuer vos mesures.";
                        int duration = Toast.LENGTH_LONG;
                        Toast toast = Toast.makeText(context, text, duration);
                        toast.show();
                    } else {
                        Context context = getApplicationContext();
                        CharSequence text = "Vous devez d'abord terminer vos mesures.";
                        int duration = Toast.LENGTH_LONG;
                        Toast toast = Toast.makeText(context, text, duration);
                        toast.show();
                    }
                }
            }
        });
    }

    public void appuiBoutonMesure(){
        /* Boolean permettant de bloquer certaines fonctionnalité pendant la mesure*/
        appuiBouttonMesure=true;
        /*Actualisation des messages indicateurs*/
        previsualisationView.setText(previsualisation);
        EchantillonView.setText(echantillon);
        listeViewWifi.setVisibility(View.GONE);
        progress.setVisibility(View.VISIBLE);
        previsualisationView.setVisibility(View.GONE);
        /*Récupération données GPS*/
        gps.onLocationChanged(gps.getLocation());
        exLatitude=latitude;
        exLongitude=longitude;
        latitude = gps.getLatitude();
        longitude = gps.getLongitude();
        // Pour le test "equals("0.0")
        String lat = Double.toString(latitude);
        String longi = Double.toString(latitude);


        if (gps != null && !lat.equals("0.0") && !longi.equals("0.0")) {
            if(done || ouverture) {
                ouverture = false;
                done=false;
            }
            wifiManager.startScan();
            SharedPreferences settings = getSharedPreferences(SETS, 0); // Update de l'état courant
            NbMesures = settings.getInt("NbMesures", NbMesures);
            NbM.setText(String.valueOf(NbMesures)); // Affichage Nombre mesure

            if (!done && lat != "0.0" && longi != "0.0") {
                indicateurEtat.setText(mesure);
            } else {
                indicateurEtat.setText(wait);
            }

            //Calcul de distance parcouru
            if(nombreEchantillon!=0) {
                distLong = (longitude - exLongitude) / 0.00001396;
                distLat = (latitude - exLatitude) / 0.00002034;
                distLongLat = ((distLat * distLat) + (distLong * distLong));
                distParcourue = Math.sqrt(distLongLat);
                distanceParcourue.setText(Double.toString(distParcourue));
                distanceParcourue.setVisibility(View.VISIBLE);
                messageGPS.setVisibility(View.VISIBLE);
            }
        }
    }
    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(Acquisition_mesure_continue.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    public void appuiBoutonCarte(){
        final View alertDialogViewChoixCampagne2 = alertdialogCollect.inflate(R.layout.alertdialog_choix_campagne3, null);
        final AlertDialog.Builder alertdial = new AlertDialog.Builder(Acquisition_mesure_continue.this);
        alertdial.setView(alertDialogViewChoixCampagne2);
        alertdial.setTitle("Choix SSID");
        final SeekBar.OnSeekBarChangeListener SeekBarListener5 =
                new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged (SeekBar seekBar, int progress, boolean fromUser) {
                        ValSeuil5=progress;
                        final EditText EditSeuil1;
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
        EditSeuil5.setText("50");
        SeekBar5.setProgress(50);

        final Spinner spinnerchoixSSID = (Spinner) alertDialogViewChoixCampagne2.findViewById(R.id.spinnerChoixSSID);


        Traitement.lectureData(nomFichierString);


        //Création d'une liste d'élément à mettre dans le Spinner
        List Listhotspot = new ArrayList();
        Listhotspot.clear();
        for (int i = 3; i < Traitement.nrow() - 3; i++) {                   //Commence à 3 car les 3 premières lignes sont des informations non utiles
            if (!Listhotspot.contains(Traitement.wordAt(i, 5))) {
                Listhotspot.add(Traitement.wordAt(i, 5));        //5ème colonne nom du SSID
            }
        }

        /*Le Spinner a besoin d'un adapter pour sa presentation alors on lui passe le context(this) et
        un fichier de presentation par défaut( android.R.layout.simple_spinner_item)
        Avec la liste des elements (exemple) */
        ArrayAdapter adapter2 = new ArrayAdapter(
                Acquisition_mesure_continue.this,
                android.R.layout.simple_spinner_item,
                Listhotspot
        );

        /* On definit une présentation du spinner quand il est déroulé         (android.R.layout.simple_spinner_dropdown_item) */
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerchoixSSID.setAdapter(adapter2);

        // Enregistrement de la configuration
        alertdial.setPositiveButton("Afficher la carte", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Seuil5 = Integer.parseInt(EditSeuil5.getText().toString());
                choixSSID = spinnerchoixSSID.getSelectedItem().toString();

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
    }


    // On arrête le receiver quand on met en pause l'application
    @Override
    protected void onPause() {
        unregisterReceiver(broadcastReceiver);
        super.onPause();
    }

    // On remet en rourte le receiver quand on reviens sur l'application
    @Override
    protected void onResume() {
        registerReceiver(broadcastReceiver, new IntentFilter(
                WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        super.onResume();
    }

    public WifiManager getCurrentWifiManager() {
        return wifiManager;
    }

    public WifiAdapter getWifiAdapter() {
        return wifiAdapter;
    }

    public List<WifiItem> getListeWifiItem() {
        return listeWifiItem;
    }

    public void traitementMesures(){
        // Gestion de la progression du progress dialog (traitement des données)
        mProgressDialog = ProgressDialog.show(this, "Patientez...",
                "Long operation starts...", true);


        // useful code, variables declarations...
        new Thread((new Runnable() {
            @Override
            public void run() {
                Message msg = null;
                String progressBarData = "Calcul du quadrillage de la carte en cours...";

                // populates the message
                msg = HandlerTraitementMesures.obtainMessage(MSG_IND, (Object) progressBarData);

                // sends the message to our handler
                HandlerTraitementMesures.sendMessage(msg);

                // starts the first long operation

                status = Traitement.recupCoordCaseFract();


                if (Accueil.ErrorStatus.NO_ERROR != status) {
                    Log.e("erreur1_tM", "error while parsing the file status:" + status);
                    // error management, creates an error message
                    msg = HandlerTraitementMesures.obtainMessage(MSG_ERR,
                            "Granularité trop élevé par rapport à la zone parcourue. Longueur de la zone : " + Integer.toString(LENGTH_MAX_VALUE)+" m");
                    // sends the message to our handler
                    HandlerTraitementMesures.sendMessage(msg);
                } else {
                    progressBarData = "Positionnement des cases sur la carte..";
                    //mProgressDialog.setMessage(progressBarData);

                    // populates the message
                    msg = HandlerTraitementMesures.obtainMessage(MSG_IND,
                            (Object) progressBarData);

                    // sends the message to our handler
                    HandlerTraitementMesures.sendMessage(msg);

                    status = Traitement.lectureDataTemp1();

                    if (Accueil.ErrorStatus.NO_ERROR != status) {
                        Log.e("erreur1_tM", "error while parsing the file status:" + status);

                        // error management, creates an error message
                        msg = HandlerTraitementMesures.obtainMessage(MSG_ERR,
                                "error while parsing the file status:" + status);
                        // sends the message to our handler
                        HandlerTraitementMesures.sendMessage(msg);
                    } else {
                        progressBarData = "Recherche des moyennes maximales..."; // + pourcentage
                        //mProgressDialog.setMessage(progressBarData);

                        // populates the message
                        msg = HandlerTraitementMesures.obtainMessage(MSG_IND,
                                (Object) progressBarData);

                        // sends the message to our handler
                        HandlerTraitementMesures.sendMessage(msg);

                        status = Traitement.rechercheMoyMax();

                        if (Accueil.ErrorStatus.NO_ERROR != status) {
                            Log.e("erreur2_tM", "error while computing the path status:"
                                    + status);
                            // error management,creates an error message
                            msg = HandlerTraitementMesures.obtainMessage(MSG_ERR,
                                    "error while computing the path status:"
                                            + status);
                            // sends the message to our handler
                            HandlerTraitementMesures.sendMessage(msg);
                        } else {
                            //progressBarData="Succès";
                            msg = HandlerTraitementMesures.obtainMessage(MSG_IND,
                                    (Object) progressBarData);
                            // sends the message to our handler
                            HandlerTraitementMesures.sendMessage(msg);

                            status = Traitement.colorCases2();

                            if (Accueil.ErrorStatus.NO_ERROR != status) {
                                Log.e("erreur2_tM", "error while computing the path status:"
                                        + status);
                                // error management,creates an error message
                                msg = HandlerTraitementMesures.obtainMessage(MSG_ERR,
                                        "error while computing the path status:"
                                                + status);
                                // sends the message to our handler
                                HandlerTraitementMesures.sendMessage(msg);
                            } else {
                                //progressBarData="Succès";
                                msg = HandlerTraitementMesures.obtainMessage(MSG_CNF,
                                        (Object) progressBarData);
                                // sends the message to our handler
                                HandlerTraitementMesures.sendMessage(msg);
                            }

                        }
                    }
                }
            }
        })).start();
    }

    final Handler HandlerTraitementMesures = new Handler() {
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

                    Intent intent = new Intent(Acquisition_mesure_continue.this, Carte.class);
                    intent.putExtra(NUMEROMESURE, NbMesures);
                    startActivity(intent);

                    break;
                default: // should never happen
                    break;
            }
        }
    };

    public void nouvelleMesure(){
        boutonRechercher.setVisibility(View.GONE);
        boutonStop.setVisibility(View.VISIBLE);
        appuiBoutonStopBool=false;
        Log.d("done",Boolean.toString(done));
        Log.d("done2",Boolean.toString(done2));
        Log.d("ouverture",Boolean.toString(ouverture));

        if (wifiManager.isWifiEnabled()) {
            latitude = gps.getLatitude();
            longitude = gps.getLongitude();
            // A but de comparaison
            String lat = Double.toString(latitude);
            String longi = Double.toString(latitude);


            //if (wifiManager.isWifiEnabled()) {
            if (gps != null && !lat.equals("0.0") && !longi.equals("0.0")) {
                if ((done || ouverture) && done2) {
                    NbEchantillon.setText(Integer.toString(nombreEchantillon));
                    done2 = false;
                    appuiBoutonMesure();
                    Handler tempoHandler = new Handler();
                    tempoHandler.postDelayed(handlerTempoRunnable, 7000); // Supérieur à la durée d'un échantillonnage
                }
            } else {
                indicateurEtat.setText(wait);
                listeViewWifi.setVisibility(View.GONE);
                progress.setVisibility(View.GONE);
                EchantillonView.setText(vide);
                previsualisationView.setText(vide);
                NbEchantillon.setText(vide);
                appuiBouttonMesure=false;
                AlertDialog.Builder alertdial = new AlertDialog.Builder(Acquisition_mesure_continue.this);
                alertdial.setTitle("Localisation introuvable");
                alertdial.setMessage("Activez la localisation avant d'utiliser l'application, redémarer-là si nécessaire");
                alertdial.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        indicateurEtat.setText(nogps);
                    }
                });
                alertdial.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        indicateurEtat.setText(nogps);
                    }
                });
                alertdial.setIcon(android.R.drawable.ic_dialog_alert);
                alertdial.show();
            }
        } else {
            appuiBouttonMesure=false;
            listeViewWifi.setVisibility(View.GONE);
            progress.setVisibility(View.GONE);
            EchantillonView.setText(vide);
            previsualisationView.setText(vide);
            NbEchantillon.setText(vide);
            appuiBouttonMesure=false;
            indicateurEtat.setText(wait);
            AlertDialog.Builder alertdial = new AlertDialog.Builder(Acquisition_mesure_continue.this);
            alertdial.setTitle("Données Wifi introuvable");
            alertdial.setMessage("Veuillez activer la WIFI.");
            alertdial.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    indicateurEtat.setText(nowifi);
                    boutonStop.setVisibility(View.GONE);
                    boutonRechercher.setVisibility(View.VISIBLE);
                }
            });
            alertdial.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    indicateurEtat.setText(nowifi);
                }
            });
            alertdial.setIcon(android.R.drawable.ic_dialog_alert);
            alertdial.show();
        }
    }

}