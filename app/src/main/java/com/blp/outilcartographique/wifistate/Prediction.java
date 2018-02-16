package com.blp.outilcartographique.wifistate;

/**
 * Created by Alexis on 19/05/2017.
 */

        import android.os.Environment;
        import android.util.Log;

        import java.io.BufferedReader;
        import java.io.File;
        import java.io.FileReader;
        import java.io.IOException;

        import static com.blp.outilcartographique.wifistate.Accueil.Seuil1;
        import static com.blp.outilcartographique.wifistate.Accueil.Seuil2;
        import static com.blp.outilcartographique.wifistate.Accueil.Seuil3;
        import static com.blp.outilcartographique.wifistate.Accueil.Seuil4;
        import static com.blp.outilcartographique.wifistate.Accueil.SeuilPasDeValeur;
        import static com.blp.outilcartographique.wifistate.Traitement.getNombreCase;
        import static com.blp.outilcartographique.wifistate.Traitement.nombreCaseMax;
        import static com.blp.outilcartographique.wifistate.Traitement.tabCouleur;
        import static com.blp.outilcartographique.wifistate.Traitement.tabLevelMoy;

/**
 * Created by Alexis on 27/03/2017.
 */

public class Prediction {
    public static boolean []casesParcouruesSansPrediction;
    public static int nbCasesPredites;
    public static String MessagePrediction = "Pas de prédiction";
    public static String PMP1 = "Nombre de cases prédites : "; // PMP = PartialMessagePrediction
    public static String PMP2 = "\t | \t  Pourcentage de cases prédites : ";
    public static boolean  predibool1=false;

    public static boolean predibool2 = false;


    private static boolean [] positionCasesVoisines;
    private static double tabLevelMoyPredict[];
    private static double coeffAtt=0.05; // Attenuation ou gain de 5%

    public static Accueil.ErrorStatus determinationZonesVoisines() {
        int cpt =0;
        positionCasesVoisines=new boolean [(int) Traitement.getNombreCase()];
        tabLevelMoyPredict = new double[(int) Traitement.getNombreCase()];
        int nombreLevel = 0;
        double sommeLevelLineaire = 0;
        boolean G=false;
        boolean D=false;
        boolean H=false;
        boolean B=false;

        // Initialisation
        for(int l=0;l<tabLevelMoyPredict.length;l++){
            tabLevelMoyPredict[l]=10;
        }
        for(int k=0;k<positionCasesVoisines.length;k++){
            positionCasesVoisines[k]=false;
        }

        Log.d("getNombrecaseLong",Double.toString(Traitement.getNombreCaseLong()));
        Log.d("getNombrecaseLat",Double.toString(Traitement.getNombreCaseLat()));
        for(int i =0; i<Traitement.getNombreCaseLat();i++){
            Log.d("i",Integer.toString(i));
            for(int j=0; j< Traitement.getNombreCaseLong();j++){
                Log.d("j",Integer.toString(j));
                if(i!=0 && i!=Traitement.getNombreCaseLat()-1){
                    if(tabLevelMoy[cpt]==SeuilPasDeValeur && tabLevelMoy[cpt-Traitement.getNombreCaseLong()]!=SeuilPasDeValeur) {
                        positionCasesVoisines[cpt] = true;

                        sommeLevelLineaire = sommeLevelLineaire + tabLevelMoy[cpt - Traitement.getNombreCaseLong()];
                        nombreLevel++;

                        B=true;

                        Log.d("CaseBas", String.valueOf(tabLevelMoy[cpt - Traitement.getNombreCaseLong()]));
                    }

                    if(tabLevelMoy[cpt]==SeuilPasDeValeur && tabLevelMoy[cpt + Traitement.getNombreCaseLong()]!=SeuilPasDeValeur) {
                        positionCasesVoisines[cpt] = true;

                        sommeLevelLineaire = sommeLevelLineaire + tabLevelMoy[cpt + Traitement.getNombreCaseLong()];
                        nombreLevel++;

                        H=true;

                        Log.d("CaseHaut", String.valueOf(tabLevelMoy[cpt + Traitement.getNombreCaseLong()]));
                    }
                } else {
                    if(i==0){
                        if(tabLevelMoy[cpt]==SeuilPasDeValeur && tabLevelMoy[cpt + Traitement.getNombreCaseLong()]!=SeuilPasDeValeur) {
                            positionCasesVoisines[cpt] = true;

                            sommeLevelLineaire = sommeLevelLineaire + tabLevelMoy[cpt + Traitement.getNombreCaseLong()];
                            nombreLevel++;

                            H=true;

                            Log.d("CaseHaut2", String.valueOf(tabLevelMoy[cpt + Traitement.getNombreCaseLong()]));
                        }
                    }
                    if(i==Traitement.getNombreCaseLat()-1){
                        if(tabLevelMoy[cpt]==SeuilPasDeValeur && tabLevelMoy[cpt-Traitement.getNombreCaseLong()]!=SeuilPasDeValeur) {
                            positionCasesVoisines[cpt] = true;

                            sommeLevelLineaire = sommeLevelLineaire + tabLevelMoy[cpt - Traitement.getNombreCaseLong()];
                            nombreLevel++;

                            B=true;

                            Log.d("CaseBas2", String.valueOf(tabLevelMoy[cpt - Traitement.getNombreCaseLong()]));
                        }
                    }
                }


                if (j == 0) { // Première case d'une longueur, inutile de regarder valeur précédente
                    if (tabLevelMoy[cpt]==SeuilPasDeValeur && tabLevelMoy[cpt+1]!=SeuilPasDeValeur) {
                        positionCasesVoisines[cpt] = true;

                        sommeLevelLineaire = sommeLevelLineaire + tabLevelMoy[cpt + 1];
                        nombreLevel++;

                        D=true;

                        Log.d("CaseDroite", String.valueOf(tabLevelMoy[cpt + 1]));
                    }
                } else {
                    if (j == Traitement.getNombreCaseLong() - 1) { // Dernière ligne du fichier, inutile de regarder valeur suivante
                        if (tabLevelMoy[cpt]==SeuilPasDeValeur && tabLevelMoy[cpt-1]!=SeuilPasDeValeur) {
                            positionCasesVoisines[cpt] = true;

                            sommeLevelLineaire = sommeLevelLineaire + tabLevelMoy[cpt - 1];
                            nombreLevel++;

                            G=true;

                            Log.d("CaseGauche", String.valueOf(tabLevelMoy[cpt - 1]));

                        }
                    } else {
                        if (tabLevelMoy[cpt]==SeuilPasDeValeur && tabLevelMoy[cpt-1]!=SeuilPasDeValeur)  {
                            positionCasesVoisines[cpt] = true;

                            sommeLevelLineaire = sommeLevelLineaire + tabLevelMoy[cpt - 1];
                            nombreLevel++;

                            G=true;

                            Log.d("CaseGauche2", String.valueOf(tabLevelMoy[cpt - 1]));

                        }

                        if(tabLevelMoy[cpt]==SeuilPasDeValeur && tabLevelMoy[cpt+1]!=SeuilPasDeValeur) {
                            positionCasesVoisines[cpt] = true;
                            sommeLevelLineaire = sommeLevelLineaire + tabLevelMoy[cpt + 1];
                            nombreLevel++;

                            D=true;

                            Log.d("CaseDroite2", String.valueOf(tabLevelMoy[cpt + 1]));
                        }
                    }
                }


                if(positionCasesVoisines[cpt] && !Traitement.getCasesParcourues(cpt)) { // S'il y a une case voisine et qu'on n'a pas déjà parcourue cette case

                    Traitement.addCasesParcourues(cpt,true);
                    nbCasesPredites++;


                    if((D==true && G==true && H==true && B==true) || (D==true && G==true && H==false && B==false) || (H==true && B==true && D==false && G==false) || (G==true && H==true && B==true && D==false) || (D==true && H==true && B==true && G==false) || (D==true && G==true && B==true && H==false) || (D==true && G==true && H==true && B==false ) || (D==false && G==true && H==true && B==false) || (D==true && G==false && H==true && B==false)|| (D==true && G==false && H==false && B==true)|| (D==false && G==true && H==false && B==true)) {
                        // Pas d'atténuation
                        tabLevelMoyPredict[cpt] = sommeLevelLineaire / nombreLevel;
                    }

                    // Une seule case prédite : Les codes suivants correspondent respectivement au cas où la case voisine est à Droite, puis gauche, puis haut, puis bas
                    if(D==true && G==false && H==false && B==false){ // droite
                        int decalG=1;
                        int decalD=2; // On sait déjà qu'il existe une valeur sur la 1ère cases voisine à droite, on regarde la valeur suivante
                        boolean pasDeValeurD=false;
                        boolean pasDeValeurG=false;

                        if(tabLevelMoy[cpt+1]<=Seuil4){
                            tabLevelMoyPredict[cpt]=sommeLevelLineaire / nombreLevel;
                        } else {
                            if((j-decalG) <=0){
                                pasDeValeurG=true;
                            } else {
                                while (tabLevelMoy[cpt - decalG] == SeuilPasDeValeur) {
                                    decalG++;
                                    if ((j - decalG) == 0) {
                                        pasDeValeurG = true;
                                        break;
                                    }
                                }
                            }
                            if((decalD +j) >= Traitement.getNombreCaseLong()){
                                pasDeValeurD=true;
                            }else {
                                while (tabLevelMoy[cpt + decalD] == SeuilPasDeValeur) {
                                    decalD++;
                                    if ((decalD + j) == Traitement.getNombreCaseLong()) {
                                        pasDeValeurD = true;
                                        break;
                                    }
                                }
                            }
                            if (!pasDeValeurD) {
                                if (!pasDeValeurG) { // Il y a des valeurs des deux côtés de la case
                                    if ((tabLevelMoy[cpt + 1] > tabLevelMoy[cpt - decalG]) && (tabLevelMoy[cpt + 1] <= tabLevelMoy[cpt + decalD])) {
                                        tabLevelMoyPredict[cpt] = (sommeLevelLineaire / nombreLevel) * (1 - coeffAtt);
                                    } else {
                                        if ((tabLevelMoy[cpt + 1] < tabLevelMoy[cpt - decalG]) && (tabLevelMoy[cpt + 1] >= tabLevelMoy[cpt + decalD])) {
                                            tabLevelMoyPredict[cpt] = (sommeLevelLineaire / nombreLevel) * (1 + coeffAtt);
                                        } else { // Incohérence
                                            Log.d("Incoherence", "L'atténuation n'est pas déterminable");
                                            tabLevelMoyPredict[cpt] = sommeLevelLineaire / nombreLevel;
                                        }
                                    }
                                } else { // Il n'y qu'une valeur à droite
                                    if (tabLevelMoy[cpt + 1] > tabLevelMoy[cpt + decalD]) {
                                        tabLevelMoyPredict[cpt] = (sommeLevelLineaire / nombreLevel) * (1 + coeffAtt);
                                    } else {
                                        tabLevelMoyPredict[cpt] = (sommeLevelLineaire / nombreLevel) * (1 - coeffAtt);
                                    }
                                }
                            } else { // Il n'y a pas de valeur à droite
                                if (!pasDeValeurG) { // Il n'y a qu'une valeur à gauche
                                    if (tabLevelMoy[cpt + 1] > tabLevelMoy[cpt - decalG]) {
                                        tabLevelMoyPredict[cpt] = (sommeLevelLineaire / nombreLevel) * (1 - coeffAtt);
                                    } else {
                                        tabLevelMoyPredict[cpt] = (sommeLevelLineaire / nombreLevel) * (1 + coeffAtt);
                                    }
                                } else { // Aucune valeur à droite ni à gauche
                                    Log.d("Attenua?", "L'atténuation n'est pas déterminable");
                                    tabLevelMoyPredict[cpt] = sommeLevelLineaire / nombreLevel;
                                }
                            }
                        }
                    }
                    if(D==false && G==true && H==false && B==false){ // gauche
                        int decalG=2; // On sait déjà qu'il existe une valeur sur la 1ère cases voisine à gauche, on regarde la valeur suivante
                        int decalD=1;
                        boolean pasDeValeurD=false;
                        boolean pasDeValeurG=false;

                        if(tabLevelMoy[cpt-1]<=Seuil4){
                            tabLevelMoyPredict[cpt]=sommeLevelLineaire / nombreLevel;
                        } else {
                            if (j != 0) {
                                if(j-decalG<=0){
                                    pasDeValeurG=true;
                                } else {
                                    while (tabLevelMoy[cpt - decalG] == SeuilPasDeValeur) {
                                        decalG++;
                                        if ((j - decalG) == 0) {
                                            pasDeValeurG = true;
                                            break;
                                        }
                                    }
                                }
                            } else {
                                pasDeValeurG = true;
                            }
                            if (j != Traitement.getNombreCaseLong() - 1) {
                                while (tabLevelMoy[cpt + decalD] == SeuilPasDeValeur) {
                                    decalD++;
                                    if ((decalD + j) == Traitement.getNombreCaseLong()) {
                                        pasDeValeurD = true;
                                        break;
                                    }
                                }
                            } else {
                                pasDeValeurD = true;
                            }
                            if (!pasDeValeurD) {
                                if (!pasDeValeurG) { // Il y a des valeurs des deux côtés de la case
                                    if ((tabLevelMoy[cpt - 1] > tabLevelMoy[cpt - decalG]) && (tabLevelMoy[cpt - 1] <= tabLevelMoy[cpt + decalD])) {
                                        tabLevelMoyPredict[cpt] = (sommeLevelLineaire / nombreLevel) * (1 + coeffAtt);
                                    } else {
                                        if ((tabLevelMoy[cpt - 1] < tabLevelMoy[cpt - decalG]) && (tabLevelMoy[cpt - 1] >= tabLevelMoy[cpt + decalD])) {
                                            tabLevelMoyPredict[cpt] = (sommeLevelLineaire / nombreLevel) * (1 - coeffAtt);
                                        } else { // Incohérence
                                            Log.d("Incoherence", "L'atténuation n'est pas déterminable");
                                            tabLevelMoyPredict[cpt] = sommeLevelLineaire / nombreLevel;
                                        }
                                    }
                                } else { // Il n'y a pas de valeur à gauche
                                    if (tabLevelMoy[cpt - 1] > tabLevelMoy[cpt + decalD]) {
                                        tabLevelMoyPredict[cpt] = (sommeLevelLineaire / nombreLevel) * (1 - coeffAtt);
                                    } else {
                                        tabLevelMoyPredict[cpt] = (sommeLevelLineaire / nombreLevel) * (1 + coeffAtt);
                                    }
                                }
                            } else { // Il n'y a pas de valeur à droite
                                if (!pasDeValeurG) { // Il n'y a qu'une valeur à gauche
                                    if (tabLevelMoy[cpt - 1] > tabLevelMoy[cpt - decalG]) {
                                        tabLevelMoyPredict[cpt] = (sommeLevelLineaire / nombreLevel) * (1 + coeffAtt);
                                    } else {
                                        tabLevelMoyPredict[cpt] = (sommeLevelLineaire / nombreLevel) * (1 - coeffAtt);
                                    }
                                } else { // Aucune valeur à droite ni à gauche
                                    Log.d("Attenua_?", "L'atténuation n'est pas déterminable");
                                    tabLevelMoyPredict[cpt] = sommeLevelLineaire / nombreLevel;
                                }
                            }
                        }
                    }
                    if(D==false && G==false && H==true && B==false){ // haut
                        int decalH=2; // On sait déjà qu'il existe une valeur sur la 1ère cases voisine à gauche, on regarde la valeur suivante
                        int decalB=1;
                        boolean pasDeValeurH=false;
                        boolean pasDeValeurB=false;

                        if(tabLevelMoy[cpt + Traitement.getNombreCaseLong()]<=Seuil4){
                            tabLevelMoyPredict[cpt]=sommeLevelLineaire / nombreLevel;
                        } else {
                            if (i != Traitement.getNombreCaseLat() - 1) {
                                if((decalH + i) >= Traitement.getNombreCaseLat()){
                                    pasDeValeurH = true;
                                } else {
                                    while (tabLevelMoy[cpt + (Traitement.getNombreCaseLong() * decalH)] == SeuilPasDeValeur) {
                                        decalH++;
                                        if ((decalH + i) >= Traitement.getNombreCaseLat()) {
                                            pasDeValeurH = true;
                                            break;
                                        }
                                    }
                                }
                            } else {
                                pasDeValeurH = true;
                            }
                            if (i != 0) {
                                if((i - decalB) <= 0){
                                    pasDeValeurH = true;
                                } else {
                                    while (tabLevelMoy[cpt - (Traitement.getNombreCaseLong() * decalB)] == SeuilPasDeValeur) {
                                        decalB++;
                                        if ((i - decalB) <= 0) {
                                            pasDeValeurB = true;
                                            break;
                                        }
                                    }
                                }
                            } else {
                                pasDeValeurB = true;
                            }

                            if (!pasDeValeurH) {
                                if (!pasDeValeurB) { // Il y a des valeurs des deux côtés de la case
                                    if ((tabLevelMoy[cpt + Traitement.getNombreCaseLong()] > tabLevelMoy[cpt - (Traitement.getNombreCaseLong()) * decalB]) && (tabLevelMoy[cpt + Traitement.getNombreCaseLong()] <= tabLevelMoy[cpt + (Traitement.getNombreCaseLong()) * decalH])) {
                                        tabLevelMoyPredict[cpt] = (sommeLevelLineaire / nombreLevel) * (1 - coeffAtt);
                                    } else {
                                        if ((tabLevelMoy[cpt + Traitement.getNombreCaseLong()] < tabLevelMoy[cpt - (Traitement.getNombreCaseLong()) * decalB]) && (tabLevelMoy[cpt + Traitement.getNombreCaseLong()] >= tabLevelMoy[cpt + (Traitement.getNombreCaseLong()) * decalH])) {
                                            tabLevelMoyPredict[cpt] = (sommeLevelLineaire / nombreLevel) * (1 + coeffAtt);
                                        } else { // Incohérence
                                            Log.d("Incoherence", "L'atténuation n'est pas déterminable");
                                            tabLevelMoyPredict[cpt] = sommeLevelLineaire / nombreLevel;
                                        }
                                    }
                                } else { // Il n'y a pas de valeur en bas
                                    if (tabLevelMoy[cpt + Traitement.getNombreCaseLong()] > tabLevelMoy[cpt + (Traitement.getNombreCaseLong()) * decalH]) {
                                        tabLevelMoyPredict[cpt] = (sommeLevelLineaire / nombreLevel) * (1 + coeffAtt);
                                    } else {
                                        tabLevelMoyPredict[cpt] = (sommeLevelLineaire / nombreLevel) * (1 - coeffAtt);
                                    }
                                }
                            } else { // Il n'y a pas de valeur en haut
                                if (!pasDeValeurB) { // S'il y a une valeur en bas
                                    if (tabLevelMoy[cpt + Traitement.getNombreCaseLong()] > tabLevelMoy[cpt - (Traitement.getNombreCaseLong()) * decalB]) {
                                        tabLevelMoyPredict[cpt] = (sommeLevelLineaire / nombreLevel) * (1 - coeffAtt);
                                    } else {
                                        tabLevelMoyPredict[cpt] = (sommeLevelLineaire / nombreLevel) * (1 + coeffAtt);
                                    }
                                } else { // Aucune valeur en haut ni en bas
                                    Log.d("Attenua?", "L'atténuation n'est pas déterminable");
                                    tabLevelMoyPredict[cpt] = sommeLevelLineaire / nombreLevel;
                                }
                            }
                        }
                    }
                    if(D==false && G==false && H==false && B==true){  // bas
                        int decalH=1; // On sait déjà qu'il existe une valeur sur la 1ère cases voisine à gauche, on regarde la valeur suivante
                        int decalB=2;
                        boolean pasDeValeurH=false;
                        boolean pasDeValeurB=false;

                        if(tabLevelMoy[cpt-Traitement.getNombreCaseLong()]<=Seuil4){
                            tabLevelMoyPredict[cpt]=sommeLevelLineaire / nombreLevel;
                        } else {
                            if (i != Traitement.getNombreCaseLat() - 1) {
                                while (tabLevelMoy[cpt + (Traitement.getNombreCaseLong()) * decalH] == SeuilPasDeValeur) {
                                    decalH++;
                                    if ((decalH + i) >= Traitement.getNombreCaseLat()) {
                                        pasDeValeurH = true;
                                        break;
                                    }
                                }
                            } else {
                                pasDeValeurH = true;
                            }
                            if (i != 0) {
                                if((i-decalB)<=0){
                                    pasDeValeurB=true;
                                } else {
                                    while (tabLevelMoy[cpt - (Traitement.getNombreCaseLong()) * decalB] == SeuilPasDeValeur) {
                                        decalB++;
                                        if ((i - decalB) <= 0) {
                                            pasDeValeurB = true;
                                            break;
                                        }
                                    }
                                }
                            } else {
                                pasDeValeurB = true;
                            }

                            if (!pasDeValeurH) {
                                if (!pasDeValeurB) { // Il y a des valeurs des deux côtés de la case
                                    if ((tabLevelMoy[cpt - Traitement.getNombreCaseLong()] > tabLevelMoy[cpt - (Traitement.getNombreCaseLong()) * decalB]) && (tabLevelMoy[cpt - Traitement.getNombreCaseLong()] <= tabLevelMoy[cpt + (Traitement.getNombreCaseLong()) * decalH])) {
                                        tabLevelMoyPredict[cpt] = (sommeLevelLineaire / nombreLevel) * (1 + coeffAtt);
                                    } else {
                                        if ((tabLevelMoy[cpt - Traitement.getNombreCaseLong()] < tabLevelMoy[cpt - (Traitement.getNombreCaseLong()) * decalB]) && (tabLevelMoy[cpt - Traitement.getNombreCaseLong()] >= tabLevelMoy[cpt + (Traitement.getNombreCaseLong()) * decalH])) {
                                            tabLevelMoyPredict[cpt] = (sommeLevelLineaire / nombreLevel) * (1 - coeffAtt);
                                        } else { // Incohérence
                                            Log.d("Incoherence", "L'atténuation n'est pas déterminable");
                                            tabLevelMoyPredict[cpt] = sommeLevelLineaire / nombreLevel;
                                        }
                                    }
                                } else { // Il n'y a pas de valeur en bas
                                    if (tabLevelMoy[cpt - Traitement.getNombreCaseLong()] > tabLevelMoy[cpt + (Traitement.getNombreCaseLong()) * decalH]) {
                                        tabLevelMoyPredict[cpt] = (sommeLevelLineaire / nombreLevel) * (1 - coeffAtt);
                                    } else {
                                        tabLevelMoyPredict[cpt] = (sommeLevelLineaire / nombreLevel) * (1 + coeffAtt);
                                    }
                                }
                            } else { // Il n'y a pas de valeur en haut
                                if (!pasDeValeurB) { // S'il y a une valeur en bas
                                    if (tabLevelMoy[cpt - Traitement.getNombreCaseLong()] > tabLevelMoy[cpt - (Traitement.getNombreCaseLong()) * decalB]) {
                                        tabLevelMoyPredict[cpt] = (sommeLevelLineaire / nombreLevel) * (1 + coeffAtt);
                                    } else {
                                        tabLevelMoyPredict[cpt] = (sommeLevelLineaire / nombreLevel) * (1 - coeffAtt);
                                    }
                                } else { // Aucune valeur en haut ni en bas
                                    Log.d("Attenua?", "L'atténuation n'est pas déterminable");
                                    tabLevelMoyPredict[cpt] = sommeLevelLineaire / nombreLevel;
                                }
                            }
                        }
                    }


                    G=false;
                    D=false;
                    H=false;
                    B=false;


                    // 0 pour vert, 1 pour jaune, 2 pour orange, 3 pour rouge, 4 pour gris, 5 pour transparent
                    Log.d("sommeLevelLin", Double.toString(sommeLevelLineaire));
                    Log.d("nombreLevel", Double.toString(nombreLevel));
                    Log.d("tabLevelMoyPredict", Double.toString(tabLevelMoyPredict[cpt]));

                    if (tabLevelMoyPredict[cpt] == SeuilPasDeValeur) {
                        tabCouleur[cpt] = 4;
                        Log.d("tabCouleuri", Integer.toString(tabCouleur[cpt]));
                    } else {

                        if (tabLevelMoyPredict[cpt] <= Seuil4) {
                            tabCouleur[cpt] = 4;
                            Log.d("tabCouleuri", Integer.toString(tabCouleur[cpt]));
                        } else if (tabLevelMoyPredict[cpt] <= Seuil3) {
                            tabCouleur[cpt] = 3;
                            Log.d("tabCouleuri", Integer.toString(tabCouleur[cpt]));
                        } else if (tabLevelMoyPredict[cpt] <= Seuil2) {
                            tabCouleur[cpt] = 2;
                            Log.d("tabCouleuri", Integer.toString(tabCouleur[cpt]));
                        } else if (tabLevelMoyPredict[cpt] <= Seuil1) {
                            tabCouleur[cpt] = 1;
                            Log.d("tabCouleuri", Integer.toString(tabCouleur[cpt]));
                        } else {
                            tabCouleur[cpt] = 0;
                        }
                    }
                }
                cpt++;
                nombreLevel = 0;
                sommeLevelLineaire = 0;
            }
        }

        return Accueil.ErrorStatus.NO_ERROR;
    }

    public static Accueil.ErrorStatus determinationZonesVoisinesInfinis() {
        int cpt =0;
        //double coeffAtt=0.05; // Attenuation de 5%
        positionCasesVoisines=new boolean [(int) Traitement.getNombreCase()];
        int nombreLevel = 0;
        double sommeLevelLineaire = 0;

        //positionRelative [] posRelat = new positionRelative[(int) Traitement.getNombreCase()];
        boolean G=false;
        boolean D=false;
        boolean H=false;
        boolean B=false;

        for(int k=0;k<positionCasesVoisines.length;k++){
            positionCasesVoisines[k]=false;
        }

        for(int l=0;l<tabLevelMoyPredict.length;l++){
            Log.d("tabLevelMoyPredict"+l,Double.toString(tabLevelMoyPredict[l]));
        }

        Log.d("getNombrecaseLong",Double.toString(Traitement.getNombreCaseLong()));
        Log.d("getNombrecaseLat",Double.toString(Traitement.getNombreCaseLat()));
        for(int i =0; i<Traitement.getNombreCaseLat();i++){
            Log.d("i",Integer.toString(i));
            for(int j=0; j< Traitement.getNombreCaseLong();j++){
                Log.d("j",Integer.toString(j));

                if(i!=0 && i!=Traitement.getNombreCaseLat()-1){
                    if(tabLevelMoyPredict[cpt]==10 && tabLevelMoyPredict[cpt-Traitement.getNombreCaseLong()]!=10 && positionCasesVoisines[cpt-Traitement.getNombreCaseLong()] == false) {
                        positionCasesVoisines[cpt] = true;

                        sommeLevelLineaire = sommeLevelLineaire + tabLevelMoyPredict[cpt - Traitement.getNombreCaseLong()];
                        nombreLevel++;

                        B=true;
                    }

                    if(tabLevelMoyPredict[cpt]==10 && tabLevelMoyPredict[cpt+Traitement.getNombreCaseLong()]!=10 && positionCasesVoisines[cpt+Traitement.getNombreCaseLong()] == false) {
                        positionCasesVoisines[cpt] = true;

                        sommeLevelLineaire = sommeLevelLineaire + tabLevelMoyPredict[cpt + Traitement.getNombreCaseLong()];
                        nombreLevel++;

                        H=true;
                    }
                } else {
                    if(i==0){
                        if(tabLevelMoyPredict[cpt]==10 && tabLevelMoyPredict[cpt+Traitement.getNombreCaseLong()]!=10 && positionCasesVoisines[cpt+Traitement.getNombreCaseLong()] == false) {
                            positionCasesVoisines[cpt] = true;

                            sommeLevelLineaire = sommeLevelLineaire + tabLevelMoyPredict[cpt + Traitement.getNombreCaseLong()];
                            nombreLevel++;

                            H=true;
                        }
                    }
                    if(i==Traitement.getNombreCaseLat()-1){
                        if(tabLevelMoyPredict[cpt]==10 && tabLevelMoyPredict[cpt-Traitement.getNombreCaseLong()]!=10 && positionCasesVoisines[cpt-Traitement.getNombreCaseLong()] == false)  {
                            positionCasesVoisines[cpt] = true;

                            sommeLevelLineaire = sommeLevelLineaire + tabLevelMoyPredict[cpt - Traitement.getNombreCaseLong()];
                            nombreLevel++;

                            B=true;
                        }
                    }
                }


                if (j == 0) { // Première case d'une longueur, inutile de regarder valeur précédente
                    if (tabLevelMoyPredict[cpt]==10 && tabLevelMoyPredict[cpt+1]!=10 && positionCasesVoisines[cpt+1] == false) {
                        positionCasesVoisines[cpt] = true;

                        sommeLevelLineaire = sommeLevelLineaire + tabLevelMoyPredict[cpt + 1];
                        nombreLevel++;

                        D=true;
                    }
                } else {
                    if (j == Traitement.getNombreCaseLong() - 1) { // Dernière ligne du fichier, inutile de regarder valeur suivante
                        if (tabLevelMoyPredict[cpt]==10 && tabLevelMoyPredict[cpt-1]!=10 && positionCasesVoisines[cpt-1] == false) {
                            positionCasesVoisines[cpt] = true;

                            sommeLevelLineaire = sommeLevelLineaire + tabLevelMoyPredict[cpt - 1];
                            nombreLevel++;

                            G=true;

                            Log.d("CaseGauche", String.valueOf(tabLevelMoy[cpt - 1]));

                        }
                    } else {
                        if (tabLevelMoyPredict[cpt]==10 && tabLevelMoyPredict[cpt-1]!=10 && positionCasesVoisines[cpt-1] == false)  {
                            positionCasesVoisines[cpt] = true;

                            sommeLevelLineaire = sommeLevelLineaire + tabLevelMoyPredict[cpt - 1];
                            nombreLevel++;

                            G=true;

                        }

                        if(tabLevelMoyPredict[cpt]==10 && tabLevelMoyPredict[cpt+1]!=10 && positionCasesVoisines[cpt+1] == false) {
                            positionCasesVoisines[cpt] = true;
                            sommeLevelLineaire = sommeLevelLineaire + tabLevelMoyPredict[cpt + 1];
                            nombreLevel++;

                            D=true;

                        }
                    }
                }


                if(positionCasesVoisines[cpt] && !Traitement.getCasesParcourues(cpt)) { // S'il y a une case voisine et qu'on n'a pas déjà parcourue cette case

                    Traitement.addCasesParcourues(cpt,true);
                    nbCasesPredites++;


                    if((D==true && G==true && H==true && B==true) || (D==true && G==true && H==false && B==false) || (H==true && B==true && D==false && G==false) || (G==true && H==true && B==true && D==false) || (D==true && H==true && B==true && G==false) || (D==true && G==true && B==true && H==false) || (D==true && G==true && H==true && B==false ) || (D==false && G==true && H==true && B==false) || (D==true && G==false && H==true && B==false)|| (D==true && G==false && H==false && B==true)|| (D==false && G==true && H==false && B==true)) {
                        // Pas d'atténuation
                        tabLevelMoyPredict[cpt] = sommeLevelLineaire / nombreLevel;
                    }

                    // Une seule case prédite : Droite, puis gauche, puis haut, puis bas
                    if(D==true && G==false && H==false && B==false){ // droite
                        int decalG=1;
                        int decalD=2; // On sait déjà qu'il existe une valeur sur la 1ère cases voisine à droite, on regarde la valeur suivante
                        boolean pasDeValeurD=false;
                        boolean pasDeValeurG=false;

                        if(tabLevelMoyPredict[cpt+1]<=Seuil4){
                            tabLevelMoyPredict[cpt]=sommeLevelLineaire / nombreLevel;
                        } else {
                            if((j-decalG)<=0) {
                                pasDeValeurG = true;
                            } else {
                                while (tabLevelMoy[cpt - decalG] == SeuilPasDeValeur) {
                                    decalG++;
                                    if ((j - decalG) == 0) {
                                        pasDeValeurG = true;
                                        break;
                                    }
                                }
                            }
                            if((decalD+j)>=Traitement.getNombreCaseLong()){
                                pasDeValeurD=true;
                            } else {
                                while (tabLevelMoy[cpt + decalD] == SeuilPasDeValeur) {
                                    decalD++;
                                    if ((decalD + j) == Traitement.getNombreCaseLong()) {
                                        pasDeValeurD = true;
                                        break;
                                    }
                                }
                            }
                            if (!pasDeValeurD) {
                                if (!pasDeValeurG) { // Il y a des valeurs des deux côtés de la case
                                    if ((tabLevelMoyPredict[cpt + 1] > tabLevelMoy[cpt - decalG]) && (tabLevelMoyPredict[cpt + 1] <= tabLevelMoy[cpt + decalD])) {
                                        tabLevelMoyPredict[cpt] = (sommeLevelLineaire / nombreLevel) * (1 + coeffAtt);
                                    } else {
                                        if ((tabLevelMoyPredict[cpt + 1] < tabLevelMoy[cpt - decalG]) && (tabLevelMoyPredict[cpt + 1] >= tabLevelMoy[cpt + decalD])) {
                                            tabLevelMoyPredict[cpt] = (sommeLevelLineaire / nombreLevel) * (1 - coeffAtt);
                                        } else { // Incohérence
                                            Log.d("Incoherence", "L'atténuation n'est pas déterminable");
                                            tabLevelMoyPredict[cpt] = sommeLevelLineaire / nombreLevel;
                                        }
                                    }
                                } else { // Il n'y qu'une valeur à droite
                                    if (tabLevelMoyPredict[cpt + 1] > tabLevelMoy[cpt + decalD]) {
                                        tabLevelMoyPredict[cpt] = (sommeLevelLineaire / nombreLevel) * (1 - coeffAtt);
                                    } else {
                                        tabLevelMoyPredict[cpt] = (sommeLevelLineaire / nombreLevel) * (1 + coeffAtt);
                                    }
                                }
                            } else { // Il n'y a pas de valeur à droite
                                if (!pasDeValeurG) { // Il n'y a qu'une valeur à gauche
                                    if (tabLevelMoyPredict[cpt + 1] > tabLevelMoy[cpt - decalG]) {
                                        tabLevelMoyPredict[cpt] = (sommeLevelLineaire / nombreLevel) * (1 + coeffAtt);
                                    } else {
                                        tabLevelMoyPredict[cpt] = (sommeLevelLineaire / nombreLevel) * (1 - coeffAtt);
                                    }
                                } else { // Aucune valeur à droite ni à gauche
                                    Log.d("Attenua?", "L'atténuation n'est pas déterminable");
                                    tabLevelMoyPredict[cpt] = sommeLevelLineaire / nombreLevel;
                                }
                            }
                        }
                    }
                    if(D==false && G==true && H==false && B==false){ // gauche
                        int decalG=2; // On sait déjà qu'il existe une valeur sur la 1ère cases voisine à gauche, on regarde la valeur suivante
                        int decalD=1;
                        boolean pasDeValeurD=false;
                        boolean pasDeValeurG=false;

                        if(tabLevelMoyPredict[cpt-1]<=Seuil4){
                            tabLevelMoyPredict[cpt]=sommeLevelLineaire / nombreLevel;
                        } else {
                            if (j != 0) {
                                if((j-decalG)<=0){
                                    pasDeValeurG=true;
                                } else {
                                    while (tabLevelMoy[cpt - decalG] == SeuilPasDeValeur) {
                                        decalG++;
                                        if ((j - decalG) == 0) {
                                            pasDeValeurG = true;
                                            break;
                                        }
                                    }
                                }
                            } else {
                                pasDeValeurG = true;
                            }
                            if (j != Traitement.getNombreCaseLong() - 1) {
                                if((decalD+j)>= Traitement.getNombreCaseLong()) {
                                    pasDeValeurD = true;
                                } else {
                                    while (tabLevelMoy[cpt + decalD] == SeuilPasDeValeur) {
                                        decalD++;
                                        if ((decalD + j) == Traitement.getNombreCaseLong()) {
                                            pasDeValeurD = true;
                                            break;
                                        }
                                    }
                                }
                            } else {
                                pasDeValeurD = true;
                            }
                            if (!pasDeValeurD) {
                                if (!pasDeValeurG) { // Il y a des valeurs des deux côtés de la case
                                    if ((tabLevelMoyPredict[cpt - 1] > tabLevelMoy[cpt - decalG]) && (tabLevelMoyPredict[cpt - 1] <= tabLevelMoy[cpt + decalD])) {
                                        tabLevelMoyPredict[cpt] = (sommeLevelLineaire / nombreLevel) * (1 - coeffAtt);
                                    } else {
                                        if ((tabLevelMoyPredict[cpt - 1] < tabLevelMoy[cpt - decalG]) && (tabLevelMoyPredict[cpt - 1] >= tabLevelMoy[cpt + decalD])) {
                                            tabLevelMoyPredict[cpt] = (sommeLevelLineaire / nombreLevel) * (1 + coeffAtt);
                                        } else { // Incohérence
                                            Log.d("Incoherence", "L'atténuation n'est pas déterminable");
                                            tabLevelMoyPredict[cpt] = sommeLevelLineaire / nombreLevel;
                                        }
                                    }
                                } else { // Il n'y a pas de valeur à gauche
                                    if (tabLevelMoyPredict[cpt - 1] > tabLevelMoy[cpt + decalD]) {
                                        tabLevelMoyPredict[cpt] = (sommeLevelLineaire / nombreLevel) * (1 + coeffAtt);
                                    } else {
                                        tabLevelMoyPredict[cpt] = (sommeLevelLineaire / nombreLevel) * (1 - coeffAtt);
                                    }
                                }
                            } else { // Il n'y a pas de valeur à droite
                                if (!pasDeValeurG) { // Il n'y a qu'une valeur à gauche
                                    if (tabLevelMoyPredict[cpt - 1] > tabLevelMoy[cpt - decalG]) {
                                        tabLevelMoyPredict[cpt] = (sommeLevelLineaire / nombreLevel) * (1 - coeffAtt);
                                    } else {
                                        tabLevelMoyPredict[cpt] = (sommeLevelLineaire / nombreLevel) * (1 + coeffAtt);
                                    }
                                } else { // Aucune valeur à droite ni à gauche
                                    Log.d("Attenua_?", "L'atténuation n'est pas déterminable");
                                    tabLevelMoyPredict[cpt] = sommeLevelLineaire / nombreLevel;
                                }
                            }
                        }
                    }
                    if(D==false && G==false && H==true && B==false){ // haut
                        int decalH=2; // On sait déjà qu'il existe une valeur sur la 1ère cases voisine à gauche, on regarde la valeur suivante
                        int decalB=1;
                        boolean pasDeValeurH=false;
                        boolean pasDeValeurB=false;

                        if(tabLevelMoyPredict[cpt + Traitement.getNombreCaseLong()]<=Seuil4){
                            tabLevelMoyPredict[cpt]=sommeLevelLineaire / nombreLevel;
                        } else {
                            if (i != Traitement.getNombreCaseLat() - 1) {
                                if((decalH+i)>=Traitement.getNombreCaseLat()) {
                                    pasDeValeurH = true;
                                } else {
                                    while (tabLevelMoy[cpt + (Traitement.getNombreCaseLong() * decalH)] == SeuilPasDeValeur) {
                                        decalH++;
                                        if ((decalH + i) >= Traitement.getNombreCaseLat()) {
                                            pasDeValeurH = true;
                                            break;
                                        }
                                    }
                                }
                            } else {
                                pasDeValeurH = true;
                            }
                            if (i != 0) {
                                if((i-decalB)<=0) {
                                    pasDeValeurB = true;
                                } else {
                                    while (tabLevelMoyPredict[cpt - (Traitement.getNombreCaseLong() * decalB)] == SeuilPasDeValeur) {
                                        decalB++;
                                        if ((i - decalB) <= 0) {
                                            pasDeValeurB = true;
                                            break;
                                        }
                                    }
                                }
                            } else {
                                pasDeValeurB = true;
                            }

                            if (!pasDeValeurH) {
                                if (!pasDeValeurB) { // Il y a des valeurs des deux côtés de la case
                                    if ((tabLevelMoyPredict[cpt + Traitement.getNombreCaseLong()] > tabLevelMoy[cpt - (Traitement.getNombreCaseLong()) * decalB]) && (tabLevelMoyPredict[cpt + Traitement.getNombreCaseLong()] <= tabLevelMoy[cpt + (Traitement.getNombreCaseLong()) * decalH])) {
                                        tabLevelMoyPredict[cpt] = (sommeLevelLineaire / nombreLevel) * (1 + coeffAtt);
                                    } else {
                                        if ((tabLevelMoyPredict[cpt + Traitement.getNombreCaseLong()] < tabLevelMoy[cpt - (Traitement.getNombreCaseLong()) * decalB]) && (tabLevelMoyPredict[cpt + Traitement.getNombreCaseLong()] >= tabLevelMoy[cpt + (Traitement.getNombreCaseLong()) * decalH])) {
                                            tabLevelMoyPredict[cpt] = (sommeLevelLineaire / nombreLevel) * (1 - coeffAtt);
                                        } else { // Incohérence
                                            Log.d("Incoherence", "L'atténuation n'est pas déterminable");
                                            tabLevelMoyPredict[cpt] = sommeLevelLineaire / nombreLevel;
                                        }
                                    }
                                } else { // Il n'y a pas de valeur en bas
                                    if (tabLevelMoyPredict[cpt + Traitement.getNombreCaseLong()] > tabLevelMoy[cpt + (Traitement.getNombreCaseLong()) * decalH]) {
                                        tabLevelMoyPredict[cpt] = (sommeLevelLineaire / nombreLevel) * (1 - coeffAtt);
                                    } else {
                                        tabLevelMoyPredict[cpt] = (sommeLevelLineaire / nombreLevel) * (1 + coeffAtt);
                                    }
                                }
                            } else { // Il n'y a pas de valeur en haut
                                if (!pasDeValeurB) { // S'il y a une valeur en bas
                                    if (tabLevelMoyPredict[cpt + Traitement.getNombreCaseLong()] > tabLevelMoy[cpt - (Traitement.getNombreCaseLong()) * decalB]) {
                                        tabLevelMoyPredict[cpt] = (sommeLevelLineaire / nombreLevel) * (1 + coeffAtt);
                                    } else {
                                        tabLevelMoyPredict[cpt] = (sommeLevelLineaire / nombreLevel) * (1 - coeffAtt);
                                    }
                                } else { // Aucune valeur en haut ni en bas
                                    Log.d("Attenua?", "L'atténuation n'est pas déterminable");
                                    tabLevelMoyPredict[cpt] = sommeLevelLineaire / nombreLevel;
                                }
                            }
                        }
                    }
                    if(D==false && G==false && H==false && B==true){  // bas
                        int decalH=1; // On sait déjà qu'il existe une valeur sur la 1ère cases voisine à gauche, on regarde la valeur suivante
                        int decalB=2;
                        boolean pasDeValeurH=false;
                        boolean pasDeValeurB=false;

                        if(tabLevelMoyPredict[cpt-Traitement.getNombreCaseLong()]<=Seuil4){
                            tabLevelMoyPredict[cpt]=sommeLevelLineaire / nombreLevel;
                        } else {
                            if (i != Traitement.getNombreCaseLat() - 1) {
                                if((decalH+i)>=Traitement.getNombreCaseLat()){
                                    pasDeValeurH=true;
                                } else {
                                    while (tabLevelMoy[cpt + (Traitement.getNombreCaseLong()) * decalH] == SeuilPasDeValeur) {
                                        decalH++;
                                        if ((decalH + i) >= Traitement.getNombreCaseLat()) {
                                            pasDeValeurH = true;
                                            break;
                                        }
                                    }
                                }
                            } else {
                                pasDeValeurH = true;
                            }
                            if (i != 0) {
                                if((i-decalB)<=0){
                                    pasDeValeurB=true;
                                } else {
                                    while (tabLevelMoy[cpt - (Traitement.getNombreCaseLong()) * decalB] == SeuilPasDeValeur) {
                                        decalB++;
                                        if ((i - decalB) <= 0) {
                                            pasDeValeurB = true;
                                            break;
                                        }
                                    }
                                }
                            } else {
                                pasDeValeurB = true;
                            }

                            if (!pasDeValeurH) {
                                if (!pasDeValeurB) { // Il y a des valeurs des deux côtés de la case
                                    if ((tabLevelMoyPredict[cpt - Traitement.getNombreCaseLong()] > tabLevelMoy[cpt - (Traitement.getNombreCaseLong()) * decalB]) && (tabLevelMoyPredict[cpt - Traitement.getNombreCaseLong()] <= tabLevelMoy[cpt + (Traitement.getNombreCaseLong()) * decalH])) {
                                        tabLevelMoyPredict[cpt] = (sommeLevelLineaire / nombreLevel) * (1 - coeffAtt);
                                    } else {
                                        if ((tabLevelMoyPredict[cpt - Traitement.getNombreCaseLong()] < tabLevelMoy[cpt - (Traitement.getNombreCaseLong()) * decalB]) && (tabLevelMoyPredict[cpt - Traitement.getNombreCaseLong()] >= tabLevelMoy[cpt + (Traitement.getNombreCaseLong()) * decalH])) {
                                            tabLevelMoyPredict[cpt] = (sommeLevelLineaire / nombreLevel) * (1 + coeffAtt);
                                        } else { // Incohérence
                                            Log.d("Incoherence", "L'atténuation n'est pas déterminable");
                                            tabLevelMoyPredict[cpt] = sommeLevelLineaire / nombreLevel;
                                        }
                                    }
                                } else { // Il n'y a pas de valeur en bas
                                    if (tabLevelMoyPredict[cpt - Traitement.getNombreCaseLong()] > tabLevelMoy[cpt + (Traitement.getNombreCaseLong()) * decalH]) {
                                        tabLevelMoyPredict[cpt] = (sommeLevelLineaire / nombreLevel) * (1 + coeffAtt);
                                    } else {
                                        tabLevelMoyPredict[cpt] = (sommeLevelLineaire / nombreLevel) * (1 - coeffAtt);
                                    }
                                }
                            } else { // Il n'y a pas de valeur en haut
                                if (!pasDeValeurB) { // S'il y a une valeur en bas
                                    if (tabLevelMoyPredict[cpt - Traitement.getNombreCaseLong()] > tabLevelMoy[cpt - (Traitement.getNombreCaseLong()) * decalB]) {
                                        tabLevelMoyPredict[cpt] = (sommeLevelLineaire / nombreLevel) * (1 - coeffAtt);
                                    } else {
                                        tabLevelMoyPredict[cpt] = (sommeLevelLineaire / nombreLevel) * (1 + coeffAtt);
                                    }
                                } else { // Aucune valeur en haut ni en bas
                                    Log.d("Attenua?", "L'atténuation n'est pas déterminable");
                                    tabLevelMoyPredict[cpt] = sommeLevelLineaire / nombreLevel;
                                }
                            }
                        }
                    }


                    G=false;
                    D=false;
                    H=false;
                    B=false;


                    // 0 pour vert, 1 pour jaune, 2 pour orange, 3 pour rouge, 4 pour gris, 5 pour transparent
                    Log.d("sommeLevelLin", Double.toString(sommeLevelLineaire));
                    Log.d("nombreLevel", Double.toString(nombreLevel));
                    Log.d("tabLevelMoyPredict", Double.toString(tabLevelMoyPredict[cpt]));

                    if (tabLevelMoyPredict[cpt] == 10) {
                        tabCouleur[cpt] = 5;
                        Log.d("tabCouleuri", Integer.toString(tabCouleur[cpt]));
                    } else {
                        if (tabLevelMoyPredict[cpt] <= Seuil4) {
                            tabCouleur[cpt] = 4;
                            Log.d("tabCouleuri", Integer.toString(tabCouleur[cpt]));
                        } else if (tabLevelMoyPredict[cpt] <= Seuil3) {
                            tabCouleur[cpt] = 3;
                            Log.d("tabCouleuri", Integer.toString(tabCouleur[cpt]));
                        } else if (tabLevelMoyPredict[cpt] <= Seuil2) {
                            tabCouleur[cpt] = 2;
                            Log.d("tabCouleuri", Integer.toString(tabCouleur[cpt]));
                        } else if (tabLevelMoyPredict[cpt] <= Seuil1) {
                            tabCouleur[cpt] = 1;
                            Log.d("tabCouleuri", Integer.toString(tabCouleur[cpt]));
                        } else {
                            tabCouleur[cpt] = 0;
                        }
                    }
                }
                cpt++;
                nombreLevel = 0;
                sommeLevelLineaire = 0;
            }
        }

        return Accueil.ErrorStatus.NO_ERROR;
    }
    public static String TextPrediction(){

        Log.d("nbCasesPredites",Integer.toString(nbCasesPredites));
        MessagePrediction = new String();
        MessagePrediction = PMP1 + nbCasesPredites;
        MessagePrediction = MessagePrediction + PMP2 + ((nbCasesPredites *100)/(Carte.getNbCasesParcouruesInit()+nbCasesPredites))+" %";
        Log.d("Info_User_Predi", MessagePrediction);
        return MessagePrediction;
    }

    public static void razCasesParcouruesSansPrediction(){
        for(int i=0; i<getNombreCase();i++){
            Traitement.addCasesParcourues(i,casesParcouruesSansPrediction[i]);
            // Log.d("casesParcourues",Boolean.toString(casesParcourues[i]));
            Log.d("casesSansPredi",Boolean.toString(casesParcouruesSansPrediction[i]));
        }
    }

    public static double getTabLevelMoyPredict(int cpt){
        return tabLevelMoyPredict[cpt];
    }


}
