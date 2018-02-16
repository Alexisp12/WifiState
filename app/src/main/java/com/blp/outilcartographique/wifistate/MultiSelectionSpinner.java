package com.blp.outilcartographique.wifistate;

/**
 * Created by Alexis on 19/05/2017.
 */


/**** Cette classe définit le comportement et les différentes fonctions pour mnipuler
 * le spinner à sélection multiple, de sa création à son remplissage et la récupération des indices
 * *****/

        import android.app.AlertDialog;
        import android.content.Context;
        import android.content.DialogInterface;
        import android.util.AttributeSet;
        import android.util.Log;
        import android.widget.ArrayAdapter;
        import android.widget.Spinner;
        import android.widget.SpinnerAdapter;

        import java.util.ArrayList;
        import java.util.Arrays;
        import java.util.LinkedList;
        import java.util.List;

        import static com.blp.outilcartographique.wifistate.Accueil.choixCanal;
        import static com.blp.outilcartographique.wifistate.Accueil.choixMac;
        import static com.blp.outilcartographique.wifistate.Accueil.choixSSID;
        import static com.blp.outilcartographique.wifistate.Accueil.listeMacAutresReseaux;
        import static com.blp.outilcartographique.wifistate.Accueil.noEmetteur;
        import static com.blp.outilcartographique.wifistate.Accueil.switchCanalActif;
        import static com.blp.outilcartographique.wifistate.Accueil.switchMACActif;
        import static com.blp.outilcartographique.wifistate.Traitement.choixMAC1boolean;
        import static com.blp.outilcartographique.wifistate.Traitement.choixSSID2boolean;


public class MultiSelectionSpinner extends Spinner implements
        DialogInterface.OnMultiChoiceClickListener
{
    //public static boolean selectionDeuxiemeReseau=false;
    public static boolean performClickBoolean=false;

    String[] _items = null;
    boolean[] mSelection = null;

    ArrayAdapter<String> simple_adapter;

    //Constructeur du MultiSpinner
    public MultiSelectionSpinner(Context context)
    {
        super(context);

        simple_adapter = new ArrayAdapter<String>(context,
                android.R.layout.simple_spinner_item);
        super.setAdapter(simple_adapter);
    }

    public MultiSelectionSpinner(Context context, AttributeSet attrs) {
        super(context, attrs);

        simple_adapter = new ArrayAdapter<String>(context,
                android.R.layout.simple_spinner_item);
        super.setAdapter(simple_adapter);
    }


    //Gestion des sélections par le click
    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
        if (mSelection != null && which < mSelection.length) {
            mSelection[which] = isChecked;

            simple_adapter.clear();
            simple_adapter.add(buildSelectedItemString());
        } else {
            throw new IllegalArgumentException(
                    "Argument 'which' is out of bounds.");
        }
    }

    // Création de bouton pour la sélection
    @Override
    public boolean performClick() {
//        if(!performClickBoolean) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMultiChoiceItems(_items, mSelection, this);

        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                boolean nouvelEmetteurBool=true;
                listeMacAutresReseaux.clear();
                for (int i = 3; i < Traitement.nrow() - 3; i++) {//Commence à 3 car les 3 premières lignes sont des informations non utiles
                    for (int j = 0; j < getSelectedStrings().size(); j++) {
                        if (!listeMacAutresReseaux.contains(Traitement.wordAt(i, 6)) && Traitement.wordAt(i, 5).equals(getSelectedStrings().get(j))) {    //Si nouvelle @MAC et est parmi le SSID
                            for(int k=0;k<choixMac.size();k++){
                                if((choixMac.get(k)).equals(Traitement.wordAt(i,6))){
                                    nouvelEmetteurBool=false;
                                }
                            }
                            if(nouvelEmetteurBool) {
                                listeMacAutresReseaux.add(Traitement.wordAt(i, 6));        //6ème colonne nom @MAC
                            } else {
                                nouvelEmetteurBool=true;
                            }
                        }
                    }
                }

                if(listeMacAutresReseaux.size()!=0) {
                    MultiSelectionSpinnerMacAjout.setItems(listeMacAutresReseaux);
                } else {
                    List<String> listeTempo = new ArrayList<String>();
                    listeTempo.add(noEmetteur);
                    MultiSelectionSpinnerMacAjout.setItems(listeTempo);
                }
            }
        });
        builder.show();
        return true;
    }

    @Override
    public void setAdapter(SpinnerAdapter adapter) {
        throw new RuntimeException(
                "setAdapter is not supported by MultiSelectSpinner.");
    }

    //Remplissage du tableau
    public void setItems(String[] items) {
        _items = items;
        mSelection = new boolean[_items.length];
        simple_adapter.clear();
        simple_adapter.add(_items[0]);
        Arrays.fill(mSelection, false);
    }

    public void setItems(List<String> items) {
        _items = items.toArray(new String[items.size()]);
        mSelection = new boolean[_items.length];
        simple_adapter.clear();
        simple_adapter.add(_items[0]);
        Arrays.fill(mSelection, false);
        Log.d("_items.length",Integer.toString(_items.length));
    }

    public void setSelection(String[] selection) {
        for (String cell : selection) {
            for (int j = 0; j < _items.length; ++j) {
                if (_items[j].equals(cell)) {
                    mSelection[j] = true;
                }
            }
        }
    }

    public void setSelection(List<String> selection) {
        for (int i = 0; i < mSelection.length; i++) {
            mSelection[i] = false;
        }
        for (String sel : selection) {
            for (int j = 0; j < _items.length; ++j) {
                if (_items[j].equals(sel)) {
                    mSelection[j] = true;
                }
            }
        }
        simple_adapter.clear();
        simple_adapter.add(buildSelectedItemString());
    }

    public void setSelection(int index) {
        for (int i = 0; i < mSelection.length; i++) {
            mSelection[i] = false;
        }
        if (index >= 0 && index < mSelection.length) {
            mSelection[index] = true;
        } else {
            throw new IllegalArgumentException("Index " + index
                    + " is out of bounds.");
        }
        simple_adapter.clear();
        simple_adapter.add(buildSelectedItemString());
    }

    public void setSelection(int[] selectedIndicies) {
        for (int i = 0; i < mSelection.length; i++) {
            mSelection[i] = false;
        }
        for (int index : selectedIndicies) {
            if (index >= 0 && index < mSelection.length) {
                mSelection[index] = true;
            } else {
                throw new IllegalArgumentException("Index " + index
                        + " is out of bounds.");
            }
        }
        simple_adapter.clear();
        simple_adapter.add(buildSelectedItemString());
    }

    //Récupération des noms sélectionnés
    public List<String> getSelectedStrings() {
        List<String> selection = new LinkedList<String>();
        for (int i = 0; i < _items.length; ++i) {
            if (mSelection[i]) {
                selection.add(_items[i]);
            }
        }
        return selection;
    }

    //Récupération des indices correspondants
    public List<Integer> getSelectedIndicies() {
        List<Integer> selection = new LinkedList<Integer>();
        for (int i = 0; i < _items.length; ++i) {
            if (mSelection[i]) {
                selection.add(i);
            }
        }
        return selection;
    }

    private String buildSelectedItemString() {
        StringBuilder sb = new StringBuilder();
        boolean foundOne = false;

        for (int i = 0; i < _items.length; ++i) {
            if (mSelection[i]) {
                if (foundOne) {
                    sb.append(", ");
                }
                foundOne = true;

                sb.append(_items[i]);
            }
        }
        return sb.toString();
    }

    public String getSelectedItemsAsString() {
        StringBuilder sb = new StringBuilder();
        boolean foundOne = false;

        for (int i = 0; i < _items.length; ++i) {
            if (mSelection[i]) {
                if (foundOne) {
                    sb.append(", ");
                }
                foundOne = true;
                sb.append(_items[i]);
            }
        }
        return sb.toString();
    }
}

