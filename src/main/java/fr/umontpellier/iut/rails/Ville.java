package fr.umontpellier.iut.rails;

import java.util.ArrayList;
import java.util.HashMap;

public class Ville {
    /**
     * Nom complet de la ville
     */
    private String nom;
    /**
     * Joueur qui a construit une gare sur la ville (ou `null` si pas de gare)
     */
    private Joueur proprietaire;

    public Ville(String nom) {
        this.nom = nom;
    }

    public String getNom() {
        return nom;
    }
    
    public Joueur getProprietaire() {
        return proprietaire;
    }
    
    public void setProprietaire(Joueur proprietaire) {
        this.proprietaire = proprietaire;
    }
    
    @Override
    public String toString() {
        return nom;
    }

    public String toLog() {
        return String.format("<span class=\"ville\">%s</span>", nom);
    }

    public Object asPOJO() {
        HashMap<String, Object> data = new HashMap<>();
        data.put("nom", nom);
        if (proprietaire != null) {
            data.put("proprietaire", proprietaire.getCouleur());
        }    
        return data;
    }

    /**
     * Vérifie si le placement d'une gare peut se faire
     */
    public boolean verifierGare(Joueur j) {
        boolean verif = false;
        if (this.getProprietaire() == null) {
            if (j.getNbGares() == 3) {
                if (j.getCartesWagon().size() >= 1) {
                    verif = true;
                }
            } else {
                Ville v1 = new Ville("Breeze");
                Ville v2 = new Ville("Luteran");
                if (j.getNbGares() == 1) {
                    Route r1 = new Route(v1, v2, 3, CouleurWagon.GRIS);
                    verif = r1.verifierTrajetCouleur(j);
                } else if (j.getNbGares() == 2) {
                    Route r2 = new Route(v1, v2, 2, CouleurWagon.GRIS);
                    verif = r2.verifierTrajetCouleur(j);
                }
            }
        }
        return verif;
    }

    /**
     * Simule le placement d'une gare sur une ville(si possible)
     * -nbGares-1
     * -placer le joueur en propriétaire de cette ville (verifier qu'elle n'est pas déjà prise)
     * -score -4
     * -carteWagonPosees -(nbGare manquante)
     */
    public void poserGare(Joueur j) {
        this.setProprietaire(j);
        j.setNbGares(-1);
        j.setScore(-4);
        while (!j.getCartesWagonPosees().isEmpty()){
            j.getJeu().defausserCarteWagon(j.getCartesWagonPosees().get(0));
            j.getCartesWagonPosees().remove(0);
        }
        j.getJeu().log(j.getNom() +" a posé une gare à " + this.getNom() + ".");
    }
}
