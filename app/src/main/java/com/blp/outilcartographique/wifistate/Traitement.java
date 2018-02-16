package com.blp.outilcartographique.wifistate;

/**
 * Created by Alexis on 19/05/2017.
 */

        import android.content.Context;
        import android.os.Environment;
        import android.util.Log;
        import android.widget.Toast;

        import com.google.android.gms.maps.model.LatLng;

        import java.io.BufferedReader;
        import java.io.File;
        import java.io.FileOutputStream;
        import java.io.FileReader;
        import java.io.IOException;
        import java.util.ArrayList;

        import static com.blp.outilcartographique.wifistate.Accueil.Couleur;
        import static com.blp.outilcartographique.wifistate.Accueil.Seuil1;
        import static com.blp.outilcartographique.wifistate.Accueil.Seuil2;
        import static com.blp.outilcartographique.wifistate.Accueil.Seuil3;
        import static com.blp.outilcartographique.wifistate.Accueil.Seuil4;
        import static com.blp.outilcartographique.wifistate.Accueil.Seuil5;
        import static com.blp.outilcartographique.wifistate.Accueil.SeuilPasDeValeur;
        import static com.blp.outilcartographique.wifistate.Accueil.accueilActif;
        import static com.blp.outilcartographique.wifistate.Accueil.choixCanal;
        import static com.blp.outilcartographique.wifistate.Accueil.choixMac;
        import static com.blp.outilcartographique.wifistate.Accueil.choixSSID;
        import static com.blp.outilcartographique.wifistate.Accueil.nbPrediction;
        import static com.blp.outilcartographique.wifistate.Accueil.switchCanalActif;
        import static com.blp.outilcartographique.wifistate.Accueil.switchMACActif;
        import static com.blp.outilcartographique.wifistate.Carte.choixMacMAJBoolean;
        import static com.blp.outilcartographique.wifistate.Prediction.casesParcouruesSansPrediction;


/**
 * Created by Alexis on 30/01/2017.
 */
public class Traitement {
    public static boolean choixMAC1boolean=false;
    public static boolean choixSSID2boolean=false;
    private static boolean wifiStateDonneExiste=false;
    public static int LENGTH_MAX_VALUE;
    static String[] hexa = {"0","1","2","3","4","5","6","7","8","9","A","B","C","D","E","F"};
    private static String []nomCampagnes;
    public static String nomFichierTemp1="fichierTemp_Coord_Case.txt";
    private static File [] fichiersDeCampagnes;
    private static String[][] words = null;
    private static String[][] wordsTemp1 = null;
    private static double nombreCase;
    private static int nrow, ncol;
    private static int nrowTemp1, ncolTemp1;
    public static double tabLevelMoy[];
    public static double tabLevelMoyCanaux[];
    private static double latMinMAX = 9999;
    private static double latMaxMAX = -9999;
    private static double longMinMAX = 9999;
    private static double longMaxMAX = -9999;
    private static double latMin;
    private static double latMax;
    private static double longMin;
    private static double longMax;
    public static int nombreCaseMax=9999;
    public static int [] tabCouleur;
    private static boolean [] casesParcourues; // Vrai si parcourue, sinon faux
    public static int [] indiceMacPredominant;
    public static int [] indiceCanalPredominant;
    public static String [] couleurAleatoire;


    public Traitement(String string){
        //nomFichier=string;
        nomCampagnes = new String[99];
    }

    public static Accueil.ErrorStatus lectureData(String nomFichier) {
        Log.d("nomFichier",nomFichier);
        try {
            Thread.sleep(100);
             File mesures = new File(Environment.getExternalStorageDirectory() + File.separator + "WifiStateData", nomFichier);
            if (!mesures.exists()) {

            } else {
                String fullfilename = mesures.getAbsolutePath();
                Log.d("mesures_length",Long.toString(mesures.length()));
                Log.d("taille_words",Long.toString((long) (mesures.length()*0.016)));// Coefficient à baisser si la tablette ne supporte pas, attention touêt de mêtre à ne pas avoir un tableau plus petit que le nombre de données
                words = new String[(int) ((mesures.length())*0.016)][11]; // Approximation de la taille du tableau de donnée à récupéré en fonction de la taille du fichier texte à récupérer
                String line;
                int nb = 0;
                try {
                    BufferedReader in = new BufferedReader(new FileReader(fullfilename));
                    while ((line = in.readLine()) != null) {
                        if(nb>2) {
                            String[] fields = line.split("" + "\t");
                            if(fields.length==10){ // Nombre de colonne par point d'accès detectés (Numéro mesure, numéro échantillon, heure, latitude, longitude ..)
                                if (ncol < fields.length) ncol = fields.length;
                                for (int j = 0; j < fields.length; j++) words[nb][j] = fields[j];
                                nb++;
                            } else {
                                // On ne prend pas en compte les lignes éventuellement bugués
                            }
                        } else {
                            String[] fields = line.split("" + "\t");
                            if (ncol < fields.length) ncol = fields.length;
                            for (int j = 0; j < fields.length; j++) words[nb][j] = fields[j];
                            nb++;
                        }
                    }

                    nrow = nb;
                    in.close();
                } catch (IOException e) {
                    System.out.println("file: " + fullfilename + " does not exist.");
                    System.exit(-1);
                }
                findLatMinExtreme();

                Thread.sleep(300);

            }
        } catch (InterruptedException e) {

        }
        return Accueil.ErrorStatus.NO_ERROR;
    }
    private static void findLatMinExtreme(){
        latMin=latMinMAX;
        longMin=longMinMAX;
        latMax=latMaxMAX;
        longMax=longMaxMAX;
        for(int i=3;i<nrow()-3;i++){
            if (latMin > Double.parseDouble(wordAt(i,3))) {
                latMin = Double.parseDouble(wordAt(i,3));
            }
            if (longMin > Double.parseDouble(wordAt(i,4))) {
                longMin = Double.parseDouble(wordAt(i,4));
            }
            if (latMax < Double.parseDouble(wordAt(i,3))) {
                latMax = Double.parseDouble(wordAt(i,3));
            }
            if (longMax < Double.parseDouble(wordAt(i,4))) {
                longMax = Double.parseDouble(wordAt(i,4));
            }
        }
    }
    public static Accueil.ErrorStatus recupCoordCaseFract(){
        int nombreCasesLong=0; // Nombre de cases sur la longitude
        int nombreCasesLat=0; // Nombre de cases sur la latitude
        double k ;
        double j;
        boolean changementDeLatitude=true;
        Log.d("seuil5",Integer.toString(Seuil5)); // Seuil5
        Log.d("LongMin",Double.toString(Traitement.getLongMin()));
        Log.d("LongMax",Double.toString(Traitement.getLongMax()));
        Log.d("LatMax",Double.toString(Traitement.getLatMax()));

        int longueur_zone_max=( (int) ((Traitement.getLongMax()-Traitement.getLongMin())/0.00002034));
        int largeur_zone_max = ((int) ((Traitement.getLatMax()-Traitement.getLatMin())/0.00001396));

        if(longueur_zone_max>largeur_zone_max){
            LENGTH_MAX_VALUE=largeur_zone_max;
        } else {
            LENGTH_MAX_VALUE=longueur_zone_max;
        }  Log.d("LENGTH_MAX_VALUE",Integer.toString(LENGTH_MAX_VALUE));
        if(Seuil5>LENGTH_MAX_VALUE){
            return Accueil.ErrorStatus.GRANUL_ELEVE;
        }


        double increment_lat = (0.00001396)*Seuil5; //Seuil5; //0.00000698 est la déviation en coordonnées GPS décimales qui représente 1 mètre
        Log.d("increment_lat",Double.toString(increment_lat));
        // L'incrément_2 est utilisé pour palier au fait que la déviation N/S est plus importante que la E/O <- ->
        //Pour obtenir des carrés, on applique un facteur 1.45714 à la valeur de increment intiale
        double increment_long = (0.00002034)*Seuil5;
        Log.d("increment_long",Double.toString(increment_long));


         /*Dans un premier temps on créé les carrés horizontaux (selon la latitude définie par 'j')
        Lorsqu'une ligne est terminée (nombre de carrés dépassant la longueur du polygone bleu), on passe à la deuxième boucle
        j est alors incrémenté (et on réinitialise la valeur de longitude k)
        La création de la grille se fait donc ligne par ligne
        Les valeurs +0.00003 sont arbitraires pour l'instant, pour avoir un affichage correct en debug
        */

        // if(Seuil5 <(mesure.getLatMax()-mesure.getLatMin())/0.0000068) {
        //Log.d("getNombrecaseAppel",Double.toString(getNombreCase()));

        File dossierfichierstemporaires = new File(Environment.getExternalStorageDirectory() + File.separator + "WifiStateData"+ File.separator+ "FichiersTemp");
        if (!dossierfichierstemporaires.exists()) { // Si le dossier n'existe pas
            dossierfichierstemporaires.mkdir(); // Création du dossier
        }

        k = Traitement.getLongMin();
        File fichierTemp1 = new File(Environment.getExternalStorageDirectory() + File.separator + "WifiStateData" + File.separator + "FichiersTemp", nomFichierTemp1);
        try {
            FileOutputStream outputStreamTemp1 = new FileOutputStream(fichierTemp1, true); // true : Pour ne pas écraser le fichier
            outputStreamTemp1.write((Double.toString(k) + "\t").getBytes());
            outputStreamTemp1.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        while(k<Traitement.getLongMax()){
            try {
                FileOutputStream outputStreamTemp1 = new FileOutputStream(fichierTemp1, true); // true : Pour ne pas écraser le fichier
                outputStreamTemp1.write((Double.toString(k + increment_long) + "\t").getBytes());
                outputStreamTemp1.close();

                k = k + increment_long; // Incrément de longitude !!
                nombreCasesLong++;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        j = Traitement.getLatMin();
        try {
            FileOutputStream outputStreamTemp1 = new FileOutputStream(fichierTemp1, true); // true : Pour ne pas écraser le fichier
            outputStreamTemp1.write(("\n").getBytes());
            outputStreamTemp1.write((Double.toString(j) + "\t").getBytes());
            outputStreamTemp1.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        while(j<Traitement.getLatMax()){
            try {
                FileOutputStream outputStreamTemp1 = new FileOutputStream(fichierTemp1, true); // true : Pour ne pas écraser le fichier
                outputStreamTemp1.write((Double.toString(j + increment_lat) + "\t").getBytes());
                outputStreamTemp1.close();
                j = j + increment_lat; // Incrément de longitude !!
                nombreCasesLat++;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        Log.d("nombreCasesLat",Double.toString(nombreCasesLat));
        Log.d("nombreCasesLong",Double.toString(nombreCasesLong));


        nombreCase=nombreCasesLat*nombreCasesLong;
        Log.d("nombreCases",Double.toString(nombreCase));

        return Accueil.ErrorStatus.NO_ERROR;
    }


    public static Accueil.ErrorStatus lectureDataTemp1() {
        File mesuresTemp1 = new File(Environment.getExternalStorageDirectory() + File.separator + "WifiStateData" + File.separator + "FichiersTemp", nomFichierTemp1);
        if (!mesuresTemp1.exists()) {

        } else {
            String fullfilename = mesuresTemp1.getAbsolutePath();
            wordsTemp1 = new String[2][(int) nombreCase+1];
            String line;
            int nb = 0;
            try {
                BufferedReader in = new BufferedReader(new FileReader(fullfilename));
                while ((line = in.readLine()) != null) {
                    String[] fieldsTemp1 = line.split("" + "\t");
                    if (ncolTemp1 < fieldsTemp1.length) ncolTemp1 = fieldsTemp1.length;
                    for (int j = 0; j < fieldsTemp1.length; j++) wordsTemp1[nb][j] = fieldsTemp1[j];
                    nb++;
                }

                nrowTemp1 = nb;
                in.close();
            } catch (IOException e) {
                System.out.println("file: " + fullfilename + " does not exist.");
                System.exit(-1);
            }
        }
        return Accueil.ErrorStatus.NO_ERROR;

    }

    public static Accueil.ErrorStatus rechercheMoyMax (){
        double longitudeTemp;
        double latitudeTemp;

        casesParcourues = new boolean[(int) nombreCase];
        tabLevelMoy = new double[(int) nombreCase];
        tabLevelMoyCanaux = new double[(int) nombreCase];
        indiceMacPredominant= new int[(int) nombreCase];
        indiceCanalPredominant= new int[(int) nombreCase];
        tabCouleur = new int[(int)nombreCase];

        for(int m=0;m<tabLevelMoy.length;m++){
            tabLevelMoy[m]=SeuilPasDeValeur;
        }
        for(int m=0;m<tabLevelMoyCanaux.length;m++){
            tabLevelMoyCanaux[m]=SeuilPasDeValeur;
        }
        Log.d("switchCanalActif",Boolean.toString(switchCanalActif));


        // Liste des @Mac déjà définies
        if(choixMacMAJBoolean){
            // On s'occupe de déclarer les macs déjà déclarés

            // Déclaration canaux toujours nécessaires
            choixCanal = new ArrayList<String>();
            for (int i = 3; i < Traitement.nrow() - 3; i++) {//Commence à 3 car les 3 premières lignes sont des informations non utiles
                if (!choixCanal.contains(Traitement.wordAt(i, 9)) && choixMac.contains(Traitement.wordAt(i, 6))) {    //Si nouvelle @MAC et est parmi le SSID
                    choixCanal.add(Traitement.wordAt(i, 9));        //6ème colonne nom @MAC
                }
            }

        } else { // choixMac non définie
            choixMac = new ArrayList<String>();
            choixCanal = new ArrayList<String>();
            for (int i = 3; i < Traitement.nrow() - 3; i++) {                   //Commence à 3 car les 3 premières lignes sont des informations non utiles
                if (!choixMac.contains(Traitement.wordAt(i, 6)) && Traitement.wordAt(i, 5).equals(choixSSID)) {    //Si nouvelle @MAC et est parmi le SSID
                    choixMac.add(Traitement.wordAt(i, 6));        //6ème colonne nom @MAC
                }
                if (!choixCanal.contains(Traitement.wordAt(i, 9)) && Traitement.wordAt(i, 5).equals(choixSSID)) {    //Si nouveau canaux appartenant au réseau SSID trouvé
                    choixCanal.add(Traitement.wordAt(i, 9));        //6ème colonne numéro canal
                }
            }
        }

        double [][]sommeLevel = new double[choixMac.size()][(int) getNombreCase()];
        int [][]nombreLevel = new int[choixMac.size()][(int) getNombreCase()];
        double [][]sommeLevelCanaux = new double[choixCanal.size()][(int) getNombreCase()];
        int [][]nombreLevelCanaux = new int[choixCanal.size()][(int) getNombreCase()];
        int positionCaseLong=0;
        int positionCaseLat=0;
        int numeroCase=0;
        int tauxAvancement=2; // On affiche l'évolution du traitement tout les "tauxAvancement" %
        longitudeTemp = getLongMin();
        latitudeTemp = getLatMin();
        Log.d("getNombrecase", Double.toString(getNombreCase()));
        Log.d("getNombrecaseLong", Double.toString(getNombreCaseLong()));
        Log.d("getNombrecaseLat", Double.toString(getNombreCaseLat()));
        //Log.d("longueurCase",Double.toString(longueurCase()));
        Log.d("longitudeTemp", Double.toString(longitudeTemp));
        Log.d("latitudeTemp", Double.toString(latitudeTemp));


        for (int j = 3; j < nrow() - 3; j++) {
            // Indication utilisateur sur l'état d'avancement du traitement
            if(nrow()>100) {
                // On indique l'avancement tout les 2% puis à 100%
                if ((((j - 2) % (tauxAvancement * (nrow() - 4) / 100)) == 0 || j == nrow() - 4)) {
                    Accueil.makeToast(j - 2, nrow() - 6);
                }
            }

            for(int k=0;k<getNombreCaseLat()+1;k++){
                if(Double.parseDouble(wordAt(j,3))>=Double.parseDouble(wordAtTemp1(1,k))){
                    positionCaseLat=k;
                }
            }
            for(int i=0;i<getNombreCaseLong()+1;i++){
                if(Double.parseDouble(wordAt(j,4))>=Double.parseDouble(wordAtTemp1(0,i))){
                    positionCaseLong=i;
                }
            }


            numeroCase=positionCaseLat*getNombreCaseLong()+positionCaseLong;
            casesParcourues[numeroCase] = true;

            for(int m=0; m< choixMac.size();m++){
                if (wordAt(j, 6).equals( choixMac.get(m))) {
                    sommeLevel[m][numeroCase] = Double.parseDouble(wordAt(j, 7)) + sommeLevel[m][numeroCase];
                    nombreLevel[m][numeroCase]++;
                }
            }

            for(int m=0; m< choixCanal.size();m++){
                if (choixMac.contains(wordAt(j, 6)) && wordAt(j, 9).equals(choixCanal.get(m))) {
                    sommeLevelCanaux[m][numeroCase] = Double.parseDouble(wordAt(j, 7)) + sommeLevelCanaux[m][numeroCase];
                    nombreLevelCanaux[m][numeroCase]++;
                }
            }
        }

        for(int l=0;l<getNombreCase();l++) {
            for (int m = 0; m < choixMac.size(); m++) {
                if (nombreLevel[m][l] != 0) {
                    if ((sommeLevel[m][l] / nombreLevel[m][l]) > tabLevelMoy[l]) {
                        tabLevelMoy[l] = sommeLevel[m][l] / nombreLevel[m][l];


                        Log.d("@MacPlusForte", "TabLevelMoy[" + l + "]=" + Double.toString(tabLevelMoy[l]));
                        Log.d("@MacConcerné", choixMac.get(m));
                        indiceMacPredominant[l] = m;
                    }
                }
            }
            for(int m=0;m<choixCanal.size();m++) {
                if (nombreLevelCanaux[m][l] != 0) {
                    if ((sommeLevelCanaux[m][l] / nombreLevelCanaux[m][l]) > tabLevelMoyCanaux[l]) {
                        tabLevelMoyCanaux[l] = sommeLevelCanaux[m][l] / nombreLevelCanaux[m][l];


                        Log.d("@MacPlusForte", "TabLevelMoyCanaux[" + l + "]=" + Double.toString(tabLevelMoyCanaux[l]));
                        Log.d("@CanalConcerné", choixCanal.get(m));
                        indiceCanalPredominant[l] = m;
                    }
                }
            }

            if(getCasesParcourues(l)){
                if (tabLevelMoy[l] == SeuilPasDeValeur) { // Réseau non présent sur la case
                    tabCouleur[l] = 4;
                    Log.d("tabCouleur["+l+"]", "Réseau non présent sur la case");
                    Log.d("tabCouleur["+l+"]", Integer.toString(tabCouleur[l]));
                } else {
                    if (tabLevelMoy[l] <= Seuil4) {
                        tabCouleur[l] = 4;
                        Log.d("tabCouleur["+l+"]", Integer.toString(tabCouleur[l]));
                    } else if (tabLevelMoy[l] <= Seuil3) {
                        tabCouleur[l] = 3;
                        Log.d("tabCouleur["+l+"]", Integer.toString(tabCouleur[l]));
                    } else if (tabLevelMoy[l] <= Seuil2) {
                        tabCouleur[l] = 2;
                        Log.d("tabCouleur["+l+"]", Integer.toString(tabCouleur[l]));
                    } else if (tabLevelMoy[l] <= Seuil1) {
                        tabCouleur[l] = 1;
                        Log.d("tabCouleur["+l+"]", Integer.toString(tabCouleur[l]));
                    } else {
                        tabCouleur[l]=0;
                    }
                }
            } else {
                tabCouleur[l]=5;
                Log.d("tabCouleur["+l+"]", Integer.toString(tabCouleur[l]));
            }
        }
        // Suppression des tableau inutiles
        sommeLevel = null;
        sommeLevelCanaux = null;
        nombreLevel=null;
        nombreLevelCanaux=null;
        RAZdata();


        return Accueil.ErrorStatus.NO_ERROR;
    }

    public static Accueil.ErrorStatus colorCases2 (){
        if(!switchMACActif && !switchCanalActif) {
            // Utilisateur sur la carte puissance, inutile et de générer couleur aléatoire
        } else {
            if (switchMACActif) {
                couleurAleatoire = new String[choixMac.size()];
            }
            if (switchCanalActif) {
                couleurAleatoire = new String[choixCanal.size()];
            }
            for (int i = 0; i < couleurAleatoire.length; i++) {
                Couleur = "8F";
                String tempColor;
                for (int j = 0; j < 6; j++) {
                    tempColor = hexa[(int) (Math.floor((Math.random() * 16)))];
                    if (!tempColor.equals(null)) {
                        Couleur = Couleur + tempColor;
                        Log.d("Caract_aléatoire", tempColor);
                    }
                }
                Log.d("Couleur_aléatoire", Couleur);
                couleurAleatoire[i] = Couleur;
            }
        }

        return Accueil.ErrorStatus.NO_ERROR;
    }

    public static Accueil.ErrorStatus updateSeuils(){

        if(nbPrediction==0) {
            // réaffecter les couleurs en fonction des seuils
            for (int cpt = 0; cpt < nombreCase; cpt++) {
                if (getCasesParcourues(cpt)) {
                    if (tabLevelMoy[cpt] == SeuilPasDeValeur) { // Réseau non présent sur la case
                        tabCouleur[cpt] = 4;
                        Log.d("tabCouleur[" + cpt + "]", Integer.toString(tabCouleur[cpt]));
                    } else {
                        if (tabLevelMoy[cpt] <= Seuil4) {
                            tabCouleur[cpt] = 4;
                            Log.d("tabCouleur[" + cpt + "]", Integer.toString(tabCouleur[cpt]));
                        } else if (tabLevelMoy[cpt] <= Seuil3) {
                            tabCouleur[cpt] = 3;
                            Log.d("tabCouleur[" + cpt + "]", Integer.toString(tabCouleur[cpt]));
                        } else if (tabLevelMoy[cpt] <= Seuil2) {
                            tabCouleur[cpt] = 2;
                            Log.d("tabCouleur[" + cpt + "]", Integer.toString(tabCouleur[cpt]));
                        } else if (tabLevelMoy[cpt] <= Seuil1) {
                            tabCouleur[cpt] = 1;
                            Log.d("tabCouleur[" + cpt + "]", Integer.toString(tabCouleur[cpt]));
                        } else {
                            tabCouleur[cpt] = 0;
                        }
                    }
                } else {
                    tabCouleur[cpt] = 5;
                    Log.d("tabCouleur[" + cpt + "]", Integer.toString(tabCouleur[cpt]));
                }
            }
        } else {
            for (int cpt = 0; cpt < nombreCase; cpt++) {
                if (getCasesParcourues(cpt)) {
                    if(Prediction.getTabLevelMoyPredict(cpt)!=10){
                        if (Prediction.getTabLevelMoyPredict(cpt) <= Seuil4) {
                            tabCouleur[cpt] = 4;
                            Log.d("tabCouleur[" + cpt + "]", Integer.toString(tabCouleur[cpt]));
                        } else if (Prediction.getTabLevelMoyPredict(cpt) <= Seuil3) {
                            tabCouleur[cpt] = 3;
                            Log.d("tabCouleur[" + cpt + "]", Integer.toString(tabCouleur[cpt]));
                        } else if (Prediction.getTabLevelMoyPredict(cpt) <= Seuil2) {
                            tabCouleur[cpt] = 2;
                            Log.d("tabCouleur[" + cpt + "]", Integer.toString(tabCouleur[cpt]));
                        } else if (Prediction.getTabLevelMoyPredict(cpt) <= Seuil1) {
                            tabCouleur[cpt] = 1;
                            Log.d("tabCouleur[" + cpt + "]", Integer.toString(tabCouleur[cpt]));
                        } else {
                            tabCouleur[cpt] = 0;
                        }
                    } else {
                        if (tabLevelMoy[cpt] == SeuilPasDeValeur) { // Réseau non présent sur la case
                            tabCouleur[cpt] = 4;
                            Log.d("tabCouleur[" + cpt + "]", Integer.toString(tabCouleur[cpt]));
                        } else {
                            if (tabLevelMoy[cpt] <= Seuil4) {
                                tabCouleur[cpt] = 4;
                                Log.d("tabCouleur[" + cpt + "]", Integer.toString(tabCouleur[cpt]));
                            } else if (tabLevelMoy[cpt] <= Seuil3) {
                                tabCouleur[cpt] = 3;
                                Log.d("tabCouleur[" + cpt + "]", Integer.toString(tabCouleur[cpt]));
                            } else if (tabLevelMoy[cpt] <= Seuil2) {
                                tabCouleur[cpt] = 2;
                                Log.d("tabCouleur[" + cpt + "]", Integer.toString(tabCouleur[cpt]));
                            } else if (tabLevelMoy[cpt] <= Seuil1) {
                                tabCouleur[cpt] = 1;
                                Log.d("tabCouleur[" + cpt + "]", Integer.toString(tabCouleur[cpt]));
                            } else {
                                tabCouleur[cpt] = 0;
                            }
                        }
                    }
                } else {
                    tabCouleur[cpt] = 5;
                    Log.d("tabCouleur[" + cpt + "]", Integer.toString(tabCouleur[cpt]));
                }
            }
        }
        return Accueil.ErrorStatus.NO_ERROR;
    }


    public static void findNamesCampagnes(){
        File wifistatedonnee = new File(Environment.getExternalStorageDirectory() + File.separator + "WifiStateData");
        if(wifistatedonnee.exists()){
            wifiStateDonneExiste=true;
            fichiersDeCampagnes = wifistatedonnee.listFiles();
            nomCampagnes=new String[fichiersDeCampagnes.length];
            for (int i = 0; i < fichiersDeCampagnes.length; i++) {
                nomCampagnes[i] = fichiersDeCampagnes[i].getName();
            }
        } else {
            nomCampagnes=null;
        }
    }

    public static String[] getNomsCampagnes(){
        return nomCampagnes;
    }

    public static int getNombreFichiers(){
        if(wifiStateDonneExiste) {
            return fichiersDeCampagnes.length;
        } else {
            return 0;
        }
    }

    public static LatLng SudOuestExtreme (){
        LatLng SudOuest = new LatLng(getLatMin(),getLongMin());
        return SudOuest;
    }
    public static LatLng SudEstExtreme (){
        LatLng SudOuest = new LatLng(getLatMax(),getLongMin());
        return SudOuest;
    }
    public static LatLng NordOuestExtreme (){
        LatLng SudOuest = new LatLng(getLatMin(),getLongMax());
        return SudOuest;
    }
    public static LatLng NordEstExtreme (){
        LatLng SudOuest = new LatLng(getLatMax(),getLongMax());
        return SudOuest;
    }


    public static double getNombreCase(){
        return nombreCase;
    }

    public static double longueurCaseLat(){
        return (0.00001396)*Seuil5;
    }
    public static double longueurCaseLong(){
        return (0.00002034)*Seuil5;
    }

    public static int getNombreCaseLong(){
        return (int) ((getLongMax()-getLongMin())/(0.00002034*Seuil5)+1);
    }

    public static int getNombreCaseLat(){
        return (int) (getNombreCase()/getNombreCaseLong());
    }


    public static int getTabCouleur(int i){
        return tabCouleur[i];
    }
    public static boolean getCasesParcourues(int i){
        return casesParcourues[i];
    }

    public static double getTabLevelMoy(int i){
        return tabLevelMoy[i];
    }
    public static double getTabLevelMoyCanaux(int i){
        return tabLevelMoyCanaux[i];
    }
    public static void addCasesParcourues(int i, boolean bool){
        casesParcourues[i]=bool;
    }
    public static double getLongMax(){
        return longMax;
    }
    public static double getLongMin(){
        return longMin;
    }
    public static double getLatMax(){
        return latMax;
    }
    public static double getLatMin(){
        return latMin;
    }

    public static int ncol() {
        return ncol;
    }

    public static int nrow() {
        return nrow;
    }

    public static int ncolTemp1() {
        return ncolTemp1;
    }

    public static int nrowTemp1() {
        return nrowTemp1;
    }

    public static void RAZdata () { words=null; }

    public static String wordAt(int i, int j) {
        return words[i][j];
    }
    public static String wordAtTemp1(int i, int j) {
        return wordsTemp1[i][j];
    }

}


