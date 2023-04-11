package fr.umontpellier.iut.rails;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class Route {
    /**
     * Première extrémité
     */
    private Ville ville1;
    /**
     * Deuxième extrémité
     */
    private Ville ville2;
    /**
     * Nombre de segments
     */
    private int longueur;
    /**
     * CouleurWagon pour capturer la route (éventuellement GRIS, mais pas LOCOMOTIVE)
     */
    private CouleurWagon couleur;
    /**
     * Joueur qui a capturé la route (`null` si la route est encore à prendre)
     */
    private Joueur proprietaire;
    /**
     * Nom unique de la route. Ce nom est nécessaire pour résoudre l'ambiguïté entre les routes doubles
     * (voir la classe Plateau pour plus de clarté)
     */
    private String nom;

    public Route(Ville ville1, Ville ville2, int longueur, CouleurWagon couleur) {
        this.ville1 = ville1;
        this.ville2 = ville2;
        this.longueur = longueur;
        this.couleur = couleur;
        nom = ville1.getNom() + " - " + ville2.getNom();
        proprietaire = null;
    }

    public Ville getVille1() {
        return ville1;
    }

    public Ville getVille2() {
        return ville2;
    }

    public int getLongueur() {
        return longueur;
    }

    public CouleurWagon getCouleur() {
        return couleur;
    }

    public Joueur getProprietaire() {
        return proprietaire;
    }

    public void setProprietaire(Joueur proprietaire) {
        this.proprietaire = proprietaire;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public void setLongueur(int longueur) {
        this.longueur = longueur;
    }

    public void setCouleur(CouleurWagon couleur) {
        this.couleur = couleur;
    }

    public int getScore(){
        int score = 0;
        if(this.longueur <= 2){
            score = this.longueur;
        }
        else if(this.longueur == 3){
            score = 4;
        }
        else if(this.longueur == 4){
            score = 7;
        }
        else if(this.longueur == 6){
            score = 15;
        }
        else if(this.longueur == 8){
            score = 21;
        }
        return score;
    }


    public Joueur routeDouble(Jeu j){
        Joueur joueur = null;
        for(int i = 0; i < j.getRoutes().size();i++){
            if(this.getVille1() == j.getRoutes().get(i).getVille1()){
                if(this.getVille2() == j.getRoutes().get(i).getVille2()){
                    if(j.getRoutes().get(i) != this){
                        joueur = j.getRoutes().get(i).proprietaire;
                    }
                }
            }
        }
        return joueur;
    }

    /**
     * vérifie un trajet qui prend en paramètre la route du trajet
     */
    public boolean verifierTrajet(Joueur j) {
        boolean valide = false;
        if (this.getProprietaire() == null) {
            if (j.getCartesWagon().size() >= this.getLongueur()) {
                if(j.getJeu().getJoueurs().size() <= 3){
                    if(this.routeDouble(j.getJeu()) == null){
                        if (j.getNbWagons() >= this.getLongueur()) {
                            valide = verifierTrajetCouleur(j);
                        }
                    }
                }
                else{
                    if(this.routeDouble(j.getJeu()) != j){
                        if (j.getNbWagons() >= this.getLongueur()) {
                            valide = verifierTrajetCouleur(j);
                        }
                    }
                }
            }
        }
        return valide;
    }

    /**
     * Vérifie un trajet avec une couleur donnée en paramètre
     */
    public boolean verifierTrajetCouleur(Joueur j) {
        boolean verification = false;
        CouleurWagon c = this.couleur;
        int cpt = 0;
        if(c.equals(CouleurWagon.GRIS)){
            c = trouveCouleurWagonQuandGris(j);
        }
        for (int i = 0; i < j.getCartesWagon().size(); i++) {
            if (j.getCartesWagon().get(i).equals(c) || j.getCartesWagon().get(i).equals(CouleurWagon.LOCOMOTIVE)) {
                cpt++;
            }
        }
        if(cpt >= this.longueur){
            verification = true;
        }
        return verification;
    }

    /**
     * Trouve la couleur(hors loco) des cartesWagons avec une longueur donnée
     */
    public CouleurWagon trouveCouleurWagonQuandGris(Joueur j) {
        ArrayList<CouleurWagon> ord = new ArrayList<>(j.getCartesWagon());
        CouleurWagon coul = ord.get(0);
        Collections.sort(ord);
        int max = 1;
        CouleurWagon coulDef = ord.get(0);
        int cpt = 1;
        for (int a = 1; a < ord.size(); a++) {
            CouleurWagon coulTemp = ord.get(a);
            if(!coulTemp.equals(coul)){
                cpt = 0;
                coul = coulTemp;
            }
            else{
                cpt++;
                if(cpt > max){
                    max = cpt;
                    cpt = 1;
                    coulDef = coulTemp;
                }
            }
        }
        return coulDef;
    }

    public CouleurWagon trouveCouleurWagonQuandGrisPosees(Joueur j) {
        if(!j.getCartesWagonPosees().isEmpty()){
            ArrayList<CouleurWagon> ord = new ArrayList<>(j.getCartesWagonPosees());
            CouleurWagon coul = ord.get(0);
            Collections.sort(ord);
            int max = 1;
            CouleurWagon coulDef = ord.get(0);
            int cpt = 1;
            for (int a = 1; a < ord.size(); a++) {
                CouleurWagon coulTemp = ord.get(a);
                if(!coulTemp.equals(coul)){
                    cpt = 0;
                    coul = coulTemp;
                }
                else{
                    cpt++;
                    if(cpt > max){
                        max = cpt;
                        cpt = 1;
                        coulDef = coulTemp;
                    }
                }
            }
            return coulDef;
        }
        return null;
    }

    public CouleurWagon capturerRoute(Joueur j){
        CouleurWagon c = j.choisirCartePoseeV2();
        CouleurWagon c2;
        int cpt=1;
        j.getCartesWagonPosees().add(c);
        while (!j.carteValide(c,this)){
            c=j.choisirCartePoseeV2();
            j.getCartesWagonPosees().add(c);
        }
        while (cpt < this.longueur){
            c2=j.choisirCartePoseeV2();
            j.getCartesWagonPosees().add(c2);
            if(c2.equals(c)||c2.equals(CouleurWagon.LOCOMOTIVE)){
                cpt++;
            }
        }
        return c;
    }
/*
    public void capturerRouteV2(Joueur j){
        ArrayList<CouleurWagon> cartePoseGarder=new ArrayList<>();
        ArrayList<CouleurWagon> cartePoserNonGarder=new ArrayList<>();
        CouleurWagon couleurChoisi=CouleurWagon.GRIS;
        int cpt=0;
        boolean trouve=false;
        while (cpt<this.longueur){
            cartePoseGarder.add(j.choisirCartePoseeV2());
            for(int i=0;i<cartePoseGarder.size();i++){
                if(j.carteValide(cartePoseGarder.get(i),this)){
                    if(!cartePoseGarder.get(i).equals(CouleurWagon.LOCOMOTIVE) &&!trouve){
                        couleurChoisi=cartePoseGarder.get(i);
                        trouve=true;
                    }
                    if(cartePoseGarder.get(i).equals(CouleurWagon.LOCOMOTIVE) || cartePoseGarder.get(i).equals(couleurChoisi)){
                        cpt++;
                    }

                }
                else{
                    cartePoserNonGarder.add(cartePoseGarder.get(i));
                }
            }
        }
        for (int i=0;i<cartePoseGarder.size();i++){
            if(cartePoserNonGarder.contains(cartePoseGarder.get(i))){
                cartePoseGarder.remove(cartePoseGarder.get(i));
            }
        }
        j.getCartesWagonPosees().addAll(cartePoseGarder);
        j.getCartesWagon().addAll(cartePoserNonGarder);
    }*/

    /**
     * prérequis : le train est valide
     * pose un train
     */
    public boolean poserTrain(Joueur j) {
        this.setProprietaire(j);
        j.setNbWagons(this.getLongueur());
        while (!j.getCartesWagonPosees().isEmpty()){
            j.getJeu().defausserCarteWagon(j.getCartesWagonPosees().get(0));
            j.getCartesWagonPosees().remove(0);
        }
        j.setScore(this.getScore());
        j.getJeu().log(this.getProprietaire().getNom() + " à poser une route : " + this + ".");
        return this.proprietaire == j;
    }

    public String toLog() {
        return String.format("<span class=\"route\">%s - %s</span>", ville1.getNom(), ville2.getNom());
    }

    @Override
    public String toString() {
        return String.format("[%s - %s (%d, %s)]", ville1, ville2, longueur, couleur);
    }

    /**
     * @return un objet simple représentant les informations de la route
     */
    public Object asPOJO() {
        HashMap<String, Object> data = new HashMap<>();
        data.put("nom", getNom());
        if (proprietaire != null) {
            data.put("proprietaire", proprietaire.getCouleur());
        }
        return data;
    }
}
