package fr.umontpellier.iut.rails;

public class Tunnel extends Route {
    public Tunnel(Ville ville1, Ville ville2, int longueur, CouleurWagon couleur) {
        super(ville1, ville2, longueur, couleur);
    }

    @Override
    public String toString() {
        return "[" + super.toString() + "]";
    }

    @Override
    public boolean poserTrain(Joueur j) {
        int ajoute = j.getJeu().piocher3Cartes(this.trouveCouleurWagonQuandGrisPosees(j));
        if (ajoute == 0) {
            return super.poserTrain(j);
        } else {
            int compteur = 0;
            for (int i = 0; i < j.getCartesWagon().size(); i++) {
                if (j.getCartesWagon().get(i).equals(CouleurWagon.LOCOMOTIVE) || j.getCartesWagon().get(i).equals(this.trouveCouleurWagonQuandGrisPosees(j))) {
                    compteur += 1;
                    if (compteur >= ajoute) {
                        if(j.ajouterCarte(ajoute, this.trouveCouleurWagonQuandGrisPosees(j))){
                            return super.poserTrain(j);
                        }
                    }
                }
            }
        }
        j.getJeu().log("Vous n'avez pas assez de cartes.");
        return false;
    }
}
