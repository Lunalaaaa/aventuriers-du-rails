package fr.umontpellier.iut.rails;

public class Ferry extends Route {
    /**
     * Nombre de locomotives qu'un joueur doit payer pour capturer le ferry
     */
    private int nbLocomotives;

    @Override
    /**
     * Vérifie si un ferry à le bon nombre de locomotive
     */
    public boolean verifierTrajet(Joueur j) {
        boolean verification = false;
        int cpt = 0;
        for (int i = 0; i < j.getCartesWagon().size(); i++) {
            if (j.getCartesWagon().get(i).equals(CouleurWagon.LOCOMOTIVE)) {
                cpt += 1;
            }
            if (cpt >= this.nbLocomotives) {
                verification = super.verifierTrajet(j);
            }
        }
        return verification;
    }

    @Override
    public CouleurWagon capturerRoute(Joueur j) {
        CouleurWagon c = j.choisirCartePoseeV2();
        CouleurWagon c2;
        j.getCartesWagonPosees().add(c);
        while (!j.carteValide(c, this)) {
            c = j.choisirCartePoseeV2();
            j.getCartesWagonPosees().add(c);
        }
        int cpt = 1;
        int nbLoco = 0;
        while (cpt < super.getLongueur()) {
            c2 = j.choisirCartePoseeV2();
            j.getCartesWagonPosees().add(c2);
            if (c2.equals(c)) {
                cpt++;
            } else if (c2.equals(CouleurWagon.LOCOMOTIVE)) {
                cpt++;
                nbLoco++;
            }
        }
        while (nbLoco != nbLocomotives) {
            c2 = j.choisirCartePoseeV2();
            j.getCartesWagonPosees().add(c2);
            if (c2.equals(CouleurWagon.LOCOMOTIVE)) {
                nbLoco++;
            }
        }
        while(j.getCartesWagonPosees().size() != super.getLongueur()){
            if(j.getCartesWagonPosees().contains(c)){
                j.getCartesWagonPosees().remove(c);
                j.getCartesWagon().add(c);
            }
            else{
                j.getCartesWagon().add(j.getCartesWagonPosees().get(0));
                j.getCartesWagonPosees().remove(0);
            }
        }
        /*
        int compteur = j.getCartesWagonPosees().size() - super.getLongueur();
        if(compteur != 0){
            while(j.getCartesWagonPosees().contains(c)){
                while(compteur != 0){
                    j.getCartesWagonPosees().remove(c);
                    j.getCartesWagon().add(c);
                    compteur--;
                }
            }
            while(compteur != 0){
                j.getCartesWagonPosees().remove(0);
                j.getCartesWagon().add(CouleurWagon.LOCOMOTIVE);
                compteur--;
            }
        }
        */
        return c;
    }


    public Ferry(Ville ville1, Ville ville2, int longueur, CouleurWagon couleur, int nbLocomotives) {
        super(ville1, ville2, longueur, couleur);
        this.nbLocomotives = nbLocomotives;
    }

    @Override
    public String toString() {
        return String.format("[%s - %s (%d, %s, %d)]", getVille1(), getVille2(), getLongueur(), getCouleur(),
                nbLocomotives);
    }
}
