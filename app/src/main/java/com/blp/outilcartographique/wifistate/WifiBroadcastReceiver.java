package com.blp.outilcartographique.wifistate;

/**
 * Created by Alexis on 19/05/2017.
 */

        import android.content.BroadcastReceiver;
        import android.content.Context;
        import android.content.Intent;
        import android.net.wifi.ScanResult;
        import android.net.wifi.WifiManager;
        import android.os.Environment;
        import android.util.Log;
        import android.widget.Toast;

        import java.io.File;
        import java.io.FileOutputStream;
        import java.text.SimpleDateFormat;
        import java.util.GregorianCalendar;
        import java.util.List;


        import static com.blp.outilcartographique.wifistate.Acquisition_mesure_continue.echantillonnage;
        import static com.blp.outilcartographique.wifistate.Acquisition_mesure_continue.indicateurEtat;
        import static com.blp.outilcartographique.wifistate.Acquisition_mesure_continue.mesureContinueOn;
        import static com.blp.outilcartographique.wifistate.Acquisition_mesure_continue.nombreEchantillon;
        import static com.blp.outilcartographique.wifistate.Acquisition_mesure_continue.NbMesures;
        import static com.blp.outilcartographique.wifistate.Acquisition_mesure_continue.appuiBouttonMesure;
        import static com.blp.outilcartographique.wifistate.Acquisition_mesure_continue.done;
        import static com.blp.outilcartographique.wifistate.Acquisition_mesure_continue.latitude;
        import static com.blp.outilcartographique.wifistate.Acquisition_mesure_continue.longitude;
        import static com.blp.outilcartographique.wifistate.Acquisition_mesure_continue.nomCampagneString;
        import static com.blp.outilcartographique.wifistate.Acquisition_mesure_continue.nombreEchantillonTotal;


public class WifiBroadcastReceiver extends BroadcastReceiver {

    private WifiManager wifiManager;
    private WifiAdapter wifiAdapter;
    private List<WifiItem> listeWifiItem;
    private String Channel1="1";
    private String Channel2="2";
    private String Channel3="3";
    private String Channel4="4";
    private String Channel5="5";
    private String Channel6="6";
    private String Channel7="7";
    private String Channel8="8";
    private String Channel9="9";
    private String Channel10="10";
    private String Channel11="11";
    private String Channel12="12";
    private String Channel13="13";
    private String Channel14="14";
    private String Channel36="36";
    private String Channel40="40";
    private String Channel44="44";
    private String Channel48="48";
    private String Channel52="52";
    private String Channel56="56";
    private String Channel60="60";
    private String Channel64="64";
    private String ChannelInconnu="?";
    private String heureString;
    private GregorianCalendar date;

    @Override
    public void onReceive(Context context, Intent intent) {
        if(mesureContinueOn){
            wifiManager = ((Acquisition_mesure_continue) context).getCurrentWifiManager();
            wifiAdapter = ((Acquisition_mesure_continue) context).getWifiAdapter();
            listeWifiItem = ((Acquisition_mesure_continue) context).getListeWifiItem();
        }
        // Si réutilisation du mode "discontinue"
//        else {
//            wifiManager = ((Acquisition_mesure_discontinue) context).getCurrentWifiManager();
//            wifiAdapter = ((Acquisition_mesure_discontinue) context).getWifiAdapter();
//            listeWifiItem = ((Acquisition_mesure_discontinue) context).getListeWifiItem();
//        }

        if(appuiBouttonMesure) {

            // On vérifie que notre objet est bien instancié
            if (wifiManager != null) {
                // On vérifie que le wifi est allumé
                if (wifiManager.isWifiEnabled()) {
                    // Récupération date et heure
                    date=new GregorianCalendar();
                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd / MM / yyyy");
                    SimpleDateFormat dateFormatHeureMin = new SimpleDateFormat("HH:mm:ss");
                    // Date
                    String dateString =dateFormat.format(date.getTime());


                    // On récupère les scans
                    List<ScanResult> listeScan = wifiManager.getScanResults();

                    // On vide notre liste
                    listeWifiItem.clear();

                    String lat = Double.toString(latitude);
                    String longi = Double.toString(longitude);
                    // Pour chaque scan
                    for (ScanResult scanResult : listeScan) {
                        // Regroupement des items
                        WifiItem item = new WifiItem();

                        // Penser à rajouter les items dans la classe WifiItem lors d'ajout de nouvelle valeurs
                        item.setAdresseMac(scanResult.BSSID);
                        item.setAPName(scanResult.SSID);
                        item.setForceSignal(scanResult.level);

                        Log.d("OutilCarthographique", scanResult.SSID + " LEVEL "
                                + scanResult.level);
                        listeWifiItem.add(item);

                        // File mesures = new File(Environment.getExternalStorageDirectory() + File.separator + "WifiStateData", "Data" + StringNData + ".txt");
                        File mesures = new File(Environment.getExternalStorageDirectory() + File.separator + "WifiStateData", nomCampagneString + ".txt");
                        File wifistatedonnee = new File(Environment.getExternalStorageDirectory() + File.separator + "WifiStateData");

                        if (!lat.equals("0.0") && !longi.equals("0.0")) {
                            if (!wifistatedonnee.exists()) { // Si le dossier n'existe pas
                                wifistatedonnee.mkdir(); // Création du dossier
                                if (!mesures.exists()) { // Si le fichier n'éxiste pas affichage légende
                                    try {
                                        // Chaque données apparait 4 fois dans la méthode, bien penser à modifier les 4 interventions en cas d'ajout / suppression / modification : 1 Titre si fichier & dossier existe pas /2 si fichier existe pas /3 scanresult / 4écriture

                                        FileOutputStream outputStream = new FileOutputStream(mesures, true); // true : Pour ne pas écraser le fichier
                                        outputStream.write(("Nom de la campagne de mesure : "+"\t"+nomCampagneString+"\n").getBytes());


                                        outputStream.write(("Date de la campagne : "+dateString+"\n").getBytes());

                                        // Chaque données apparait 4 fois dans la méthode, bien penser à modifier les 4 interventions en cas d'ajout / suppression / modification : 1 Titre si fichier & dossier existe pas /2 si fichier existe pas /3 scanresult / 4écriture

                                        outputStream.write("Mesure n°\t".getBytes());
                                        outputStream.write("Echantillon n°\t".getBytes());
                                        outputStream.write("Heure (UTC/GMT +01:00)\t".getBytes());
                                        outputStream.write("Latitude\t".getBytes());
                                        outputStream.write("Longitude\t".getBytes());
                                        outputStream.write("SSID\t".getBytes());
                                        outputStream.write("BSSID/AdresseMAC\t".getBytes());
                                        outputStream.write("Level\t".getBytes());
                                        outputStream.write("Frequency\t".getBytes());
                                        outputStream.write("Channel\t".getBytes());
                                        // En commentaire d'autres données récupérables à partir de scanresult
                                        //outputStream.write("channelWidth\t".getBytes());
                                        //outputStream.write("describeContents\t".getBytes());
                                        //outputStream.write("timestamp\t".getBytes());
                                        //outputStream.write("capabilities\t".getBytes());
                                        //outputStream.write("centerFreq0\t".getBytes());
                                        //outputStream.write("centerFreq1\t".getBytes());
                                        //outputStream.write("operatorFriendlyName\t".getBytes());
                                        outputStream.write("\n".getBytes());
                                        outputStream.close();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            } else {
                                if (!mesures.exists()) { // Si le fichier n'éxiste pas affichage légende
                                    try {
                                        FileOutputStream outputStream = new FileOutputStream(mesures, true); // true : Pour ne pas écraser le fichier
                                        outputStream.write(("Nom de la campagne de mesure : "+nomCampagneString+"\n").getBytes());

                                        outputStream.write(("Date de la campagne : "+dateString+"\n").getBytes());

                                        outputStream.write("Mesure n°\t".getBytes());
                                        outputStream.write("Echantillon n°\t".getBytes());
                                        outputStream.write("Heure UTC/GMT +01:00\t".getBytes());
                                        outputStream.write("Latitude\t".getBytes());
                                        outputStream.write("Longitude\t".getBytes());
                                        outputStream.write("SSID\t".getBytes());
                                        outputStream.write("BSSID/AdresseMAC\t".getBytes());
                                        outputStream.write("Level\t".getBytes());
                                        outputStream.write("Frequency\t".getBytes());
                                        outputStream.write("Channel\t".getBytes());
                                        // En commentaire d'autres données récupérables à partir de scanresult
                                        // outputStream.write("channelWidth\t".getBytes());
                                        //  outputStream.write("describeContents\t".getBytes());
                                        //outputStream.write("timestamp\t".getBytes());
                                        // outputStream.write("capabilities\t".getBytes());
                                        // outputStream.write("centerFreq0\t".getBytes());
                                        // outputStream.write("centerFreq1\t".getBytes());
                                        //outputStream.write("operatorFriendlyName\t".getBytes());

                                        outputStream.write("\n".getBytes());
                                        outputStream.close();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }


                            try {

                                String APName = scanResult.SSID + "\t";
                                String AdresseMac = scanResult.BSSID + "\t";
                                String ForceSignal = scanResult.level + "\t";
                                String Frequency = scanResult.frequency + "\t";
                                heureString = dateFormatHeureMin.format(date.getTime());


                                // String channelWidth = scanResult.channelWidth+"\t";
                                //String centerFreq0 = scanResult.centerFreq0+"\t";
                                //String centerFreq1 = scanResult.centerFreq1+"\t";
                                // String describeContents = scanResult.describeContents()+"\t";
                                //String timestamp = scanResult.timestamp+"\t";
                                //String capabilities = scanResult.capabilities+"\t";
                                //String operatorFriendlyName = scanResult.operatorFriendlyName+"\t";


                                FileOutputStream outputStream = new FileOutputStream(mesures, true); // true : Pour ne pas écraser le fichier
                                outputStream.write((Integer.toString(NbMesures) + "\t").getBytes());
                                outputStream.write((nombreEchantillon+1+"\t").getBytes());
                                outputStream.write((heureString+"\t").getBytes());
                                outputStream.write((latitude + "\t").getBytes());
                                outputStream.write((longitude + "\t").getBytes());
                                outputStream.write(APName.getBytes());
                                outputStream.write(AdresseMac.getBytes());
                                outputStream.write(ForceSignal.getBytes());
                                outputStream.write(Frequency.getBytes());
                                // Identification du canal utilisé en fonction de la fréquence mesuré
                                switch (scanResult.frequency) {
                                    case 2412:
                                        outputStream.write((Channel1 + "\t").getBytes());
                                        break;
                                    case 2417:
                                        outputStream.write((Channel2 + "\t").getBytes());
                                        break;
                                    case 2422:
                                        outputStream.write((Channel3 + "\t").getBytes());
                                        break;
                                    case 2427:
                                        outputStream.write((Channel4 + "\t").getBytes());
                                        break;
                                    case 2432:
                                        outputStream.write((Channel5 + "\t").getBytes());
                                        break;
                                    case 2437:
                                        outputStream.write((Channel6 + "\t").getBytes());
                                        break;
                                    case 2442:
                                        outputStream.write((Channel7 + "\t").getBytes());
                                        break;
                                    case 2447:
                                        outputStream.write((Channel8 + "\t").getBytes());
                                        break;
                                    case 2452:
                                        outputStream.write((Channel9 + "\t").getBytes());
                                        break;
                                    case 2457:
                                        outputStream.write((Channel10 + "\t").getBytes());
                                        break;
                                    case 2462:
                                        outputStream.write((Channel11 + "\t").getBytes());
                                        break;
                                    case 2467:
                                        outputStream.write((Channel12 + "\t").getBytes());
                                        break;
                                    case 2472:
                                        outputStream.write((Channel13 + "\t").getBytes());
                                        break;
                                    case 2484:
                                        outputStream.write((Channel14 + "\t").getBytes());
                                        break;
                                    case 5180:
                                        outputStream.write((Channel36+ "\t").getBytes());
                                        break;
                                    case 5200:
                                        outputStream.write((Channel40+ "\t").getBytes());
                                        break;
                                    case 5220:
                                        outputStream.write((Channel44+ "\t").getBytes());
                                        break;
                                    case 5240:
                                        outputStream.write((Channel48+ "\t").getBytes());
                                        break;
                                    case 5260:
                                        outputStream.write((Channel52+ "\t").getBytes());
                                        break;
                                    case 5280:
                                        outputStream.write((Channel56+ "\t").getBytes());
                                        break;
                                    case 5300:
                                        outputStream.write((Channel60+ "\t").getBytes());
                                        break;
                                    case 5320:
                                        outputStream.write((Channel64+ "\t").getBytes());
                                        break;
                                    default:
                                        outputStream.write((ChannelInconnu + "\t").getBytes());
                                        break;
                                }


                                // outputStream.write(channelWidth.getBytes());
                                // outputStream.write(describeContents.getBytes());
                                // outputStream.write(timestamp.getBytes());
                                // outputStream.write(capabilities.getBytes());
                                //outputStream.write(centerFreq0.getBytes());
                                //outputStream.write(centerFreq1.getBytes());
                                //outputStream.write(operatorFriendlyName.getBytes());

                                outputStream.write("\n".getBytes());
                                outputStream.close();


                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    // On rafraichit la liste
                    wifiAdapter.notifyDataSetChanged();

                    /*
                    // Gestion du nombre de mesure récupéré

                    while(NombreEchantillon!=0) {
                        Handler myHandler = new Handler();
                        myHandler.postDelayed(mMyRunnable, 900);
                        NombreEchantillon--;
                    }
                    */

                    done = true;
                    appuiBouttonMesure = false;
                    // Mesure réalisé
                    if(!mesureContinueOn) {
                        if (nombreEchantillon != nombreEchantillonTotal + 1) {
                            indicateurEtat.setText(echantillonnage);
                        }
                    }

                } else {
                    Toast.makeText(context, "Vous devez activer votre wifi",
                            Toast.LENGTH_SHORT);
                }
            }
        }

    }
}
