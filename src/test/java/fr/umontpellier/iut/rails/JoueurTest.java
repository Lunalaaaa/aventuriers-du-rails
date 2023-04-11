package fr.umontpellier.iut.rails;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;



import static org.junit.jupiter.api.Assertions.*;

public class JoueurTest {
    private IOJeu jeu;
    private Joueur joueur1;
    private Joueur joueur2;
    private Joueur joueur3;
    private Joueur joueur4;

    /**
     * Renvoie la route du jeu dont le nom est passé en argument
     *
     * @param nom le nom de la route
     * @return la route du jeu dont le nom est passé en argument (ou null si aucune
     *         route ne correspond)
     */
    public Route getRouteParNom(String nom) {
        for (Route route : jeu.getRoutes()) {
            if (route.getNom().equals(nom)) {
                return route;
            }
        }
        return null;
    }

    /**
     * Renvoie la ville du jeu dont le nom est passé en argument
     *
     * @param nom le nom de la ville
     * @return la ville du jeu dont le nom est passé en argument (ou null si aucune
     *         ville ne correspond)
     */
    public Ville getVilleParNom(String nom) {
        for (Ville ville : jeu.getVilles()) {
            if (ville.getNom().equals(nom)) {
                return ville;
            }
        }
        return null;
    }

    @BeforeEach
    void init() {
        jeu = new IOJeu(new String[] { "Guybrush", "Largo", "LeChuck", "Elaine" });
        List<Joueur> joueurs = jeu.getJoueurs();
        joueur1 = joueurs.get(0);
        joueur2 = joueurs.get(1);
        joueur3 = joueurs.get(2);
        joueur4 = joueurs.get(3);
        joueur1.getCartesWagon().clear();
        joueur2.getCartesWagon().clear();
        joueur3.getCartesWagon().clear();
        joueur4.getCartesWagon().clear();
    }


    @Test
    void testChoisirDestinations() {
        jeu.setInput("Athina - Angora (5)", "Frankfurt - Kobenhavn (5)");
        ArrayList<Destination> destinationsPossibles = new ArrayList<>();
        Destination d1 = new Destination("Athina", "Angora", 5);
        Destination d2 = new Destination("Budapest", "Sofia", 5);
        Destination d3 = new Destination("Frankfurt", "Kobenhavn", 5);
        Destination d4 = new Destination("Rostov", "Erzurum", 5);
        destinationsPossibles.add(d1);
        destinationsPossibles.add(d2);
        destinationsPossibles.add(d3);
        destinationsPossibles.add(d4);

        List<Destination> destinationsDefaussees = joueur1.choisirDestinations(destinationsPossibles, 2);
        assertEquals(2, joueur1.getDestinations().size());
        assertEquals(2, destinationsDefaussees.size());
        assertTrue(destinationsDefaussees.contains(d1));
        assertTrue(destinationsDefaussees.contains(d3));
        assertTrue(joueur1.getDestinations().contains(d2));
        assertTrue(joueur1.getDestinations().contains(d4));
    }
        
    @Test
    void testJouerTourPrendreCartesWagon() {
        jeu.setInput("GRIS", "ROUGE");

        // On met 5 cartes ROUGE dans les cartes wagon visibles
        List<CouleurWagon> cartesWagonVisibles = jeu.getCartesWagonVisibles();
        cartesWagonVisibles.clear();
        cartesWagonVisibles.add(CouleurWagon.ROUGE);
        cartesWagonVisibles.add(CouleurWagon.ROUGE);
        cartesWagonVisibles.add(CouleurWagon.ROUGE);
        cartesWagonVisibles.add(CouleurWagon.ROUGE);
        cartesWagonVisibles.add(CouleurWagon.ROUGE);

        // On met VERT, BLEU, LOCOMOTIVE (haut de pile) dans la pile de cartes wagon
        List<CouleurWagon> pileCartesWagon = jeu.getPileCartesWagon();
        pileCartesWagon.add(0, CouleurWagon.BLEU);
        pileCartesWagon.add(0, CouleurWagon.LOCOMOTIVE);
        int nbCartesWagon = pileCartesWagon.size();

        joueur1.jouerTour();

        // le joueur devrait piocher la LOCOMOTIVE, prendre une carte ROUGE
        // puis le jeu devrait remettre une carte visible BLEU
        assertTrue(TestUtils.contientExactement(
            joueur1.getCartesWagon(),
            CouleurWagon.ROUGE,
            CouleurWagon.LOCOMOTIVE));
        System.out.println(joueur1.getCartesWagon());
        assertTrue(TestUtils.contientExactement(
                cartesWagonVisibles,
                CouleurWagon.BLEU,
                CouleurWagon.ROUGE,
                CouleurWagon.ROUGE,
                CouleurWagon.ROUGE,
                CouleurWagon.ROUGE));
        assertEquals(nbCartesWagon - 2, pileCartesWagon.size());
    }
    @Test
    void testJouerTourCapturerRoute() {
        List<CouleurWagon> cartesWagon = joueur2.getCartesWagon();
        cartesWagon.add(CouleurWagon.BLEU);
        cartesWagon.add(CouleurWagon.BLEU);
        cartesWagon.add(CouleurWagon.ROUGE);
        cartesWagon.add(CouleurWagon.ROUGE);
        cartesWagon.add(CouleurWagon.LOCOMOTIVE);

        jeu.setInput(
                "Brest - Pamplona", // coûte 4 ROSE (ne peut pas capturer)
                "Bruxelles - Frankfurt", // coûte 2 BLEU
                "BLEU", // ok
                "ROUGE", // ne convient pas pour une route de 2 BLEU
                "LOCOMOTIVE" // ok
        );

        joueur2.jouerTour();
        assertEquals(null, getRouteParNom("Brest - Pamplona").getProprietaire());
        assertEquals(joueur2, getRouteParNom("Bruxelles - Frankfurt").getProprietaire());
        assertTrue(TestUtils.contientExactement(
                joueur2.getCartesWagon(),
                CouleurWagon.BLEU, CouleurWagon.ROUGE, CouleurWagon.ROUGE));
        assertTrue(TestUtils.contientExactement(
                jeu.getDefausseCartesWagon(),
                CouleurWagon.BLEU,
                CouleurWagon.LOCOMOTIVE));
    }

    //@Disabled
    @Test
    public void testWagonGris(){
        Ville v = new Ville("");
        Ville v1 = new Ville("");
        Route r = new Route(v, v1, 2, CouleurWagon.GRIS);
        joueur1.getCartesWagon().clear();
        joueur1.getCartesWagon().add(CouleurWagon.ROUGE);
        joueur1.getCartesWagon().add(CouleurWagon.ROUGE);
        joueur1.getCartesWagon().add(CouleurWagon.BLANC);
        joueur1.getCartesWagon().add(CouleurWagon.BLANC);
        assertEquals(CouleurWagon.BLANC, r.trouveCouleurWagonQuandGris(joueur1));
    }

    //@Disabled
    @Test
    public void testCartesPoseesTriCouleur(){
        joueur1.getCartesWagon().clear();
        List<CouleurWagon> cartes = joueur1.getCartesWagon();
        cartes.add(CouleurWagon.LOCOMOTIVE);
        cartes.add(CouleurWagon.BLEU);
        cartes.add(CouleurWagon.JAUNE);
        cartes.add(CouleurWagon.LOCOMOTIVE);
        cartes.add(CouleurWagon.VERT);
        cartes.add(CouleurWagon.BLEU);
        Ville v = new Ville("");
        Ville v1 = new Ville("");
        Route r = new Route(v, v1, 4, CouleurWagon.BLEU);
        ArrayList<CouleurWagon> listeValide= new ArrayList<>();
        listeValide.add(CouleurWagon.JAUNE);
        listeValide.add(CouleurWagon.VERT);
        ArrayList<CouleurWagon> listePose = new ArrayList<>();
        listePose.add(CouleurWagon.LOCOMOTIVE);
        listePose.add(CouleurWagon.LOCOMOTIVE);
        listePose.add(CouleurWagon.BLEU);
        listePose.add(CouleurWagon.BLEU);
        for(int i = 0; i < joueur1.getCartesWagon().size(); i++){
            CouleurWagon c = joueur1.getCartesWagon().get(i);
            jeu.setInput(c.name(), "Fini");
            joueur1.choisirCartePosee();
        }
        //assertEquals(listePose, joueur1.getCartesWagonPosees());
        //joueur1.cartesPoseesTri(r);
        //assertEquals(listeValide, joueur1.getCartesWagon());
        //assertEquals(listePose, joueur1.getCartesWagonPosees());
        /*
        assertTrue(TestUtils.contientExactement(
                joueur1.getCartesWagon(), CouleurWagon.JAUNE, CouleurWagon.VERT
        ));

        assertTrue(TestUtils.contientExactement(
                joueur1.getCartesWagonPosees(), CouleurWagon.LOCOMOTIVE, CouleurWagon.LOCOMOTIVE, CouleurWagon.BLEU, CouleurWagon.BLEU, CouleurWagon.BLEU
        ));
        */

    }
    //@Test
    void joueurPiocheUneFoisPuisPasse(){
        List<CouleurWagon> couleurWagons = joueur1.getCartesWagon();
        couleurWagons.add(CouleurWagon.LOCOMOTIVE);
        couleurWagons.add(CouleurWagon.ROUGE);
        while (!jeu.getCartesWagonVisibles().isEmpty()){
            jeu.getCartesWagonVisibles().remove(0);
        }
        jeu.getCartesWagonVisibles().clear();
        List<CouleurWagon> carteVisible = jeu.getCartesWagonVisibles();
        carteVisible.add(CouleurWagon.LOCOMOTIVE);
        carteVisible.add(CouleurWagon.VERT);
        carteVisible.add(CouleurWagon.VERT);
        carteVisible.add(CouleurWagon.BLEU);
        carteVisible.add(CouleurWagon.ROSE);

        jeu.getPileCartesWagon().clear();
        List<CouleurWagon> pileWagon =jeu.getPileCartesWagon();
        pileWagon.add(CouleurWagon.JAUNE);
        pileWagon.add(CouleurWagon.ORANGE);
        pileWagon.add(CouleurWagon.LOCOMOTIVE);

        jeu.setInput("VERT","");
        joueur1.jouerTour();

        assertTrue(TestUtils.contientExactement(
                joueur1.getCartesWagon(),
                CouleurWagon.ROUGE,CouleurWagon.LOCOMOTIVE,CouleurWagon.VERT));
        assertTrue(TestUtils.contientExactement(
                carteVisible,
                CouleurWagon.JAUNE,CouleurWagon.LOCOMOTIVE,CouleurWagon.VERT,CouleurWagon.ROSE, CouleurWagon.BLEU));
        assertTrue(TestUtils.contientExactement(
                pileWagon,
                CouleurWagon.LOCOMOTIVE, CouleurWagon.ORANGE));
        assertEquals(3,joueur1.getCartesWagon().size());
    }


    //@Test
    void joueurPiocheVertPuisLoco(){
        List<CouleurWagon> couleurWagons = joueur1.getCartesWagon();
        couleurWagons.add(CouleurWagon.LOCOMOTIVE);
        couleurWagons.add(CouleurWagon.ROUGE);
        while (!jeu.getCartesWagonVisibles().isEmpty()){
            jeu.getCartesWagonVisibles().remove(0);
        }
        jeu.getCartesWagonVisibles().clear();
        List<CouleurWagon> carteVisible = jeu.getCartesWagonVisibles();
        carteVisible.add(CouleurWagon.LOCOMOTIVE);
        carteVisible.add(CouleurWagon.VERT);
        carteVisible.add(CouleurWagon.VERT);
        carteVisible.add(CouleurWagon.BLEU);
        carteVisible.add(CouleurWagon.ROSE);

        jeu.getPileCartesWagon().clear();
        List<CouleurWagon> pileWagon =jeu.getPileCartesWagon();
        pileWagon.add(CouleurWagon.JAUNE);
        pileWagon.add(CouleurWagon.ORANGE);
        pileWagon.add(CouleurWagon.LOCOMOTIVE);

        jeu.setInput("VERT","LOCOMOTIVE","");
        joueur1.jouerTour();

        assertTrue(TestUtils.contientExactement(
                joueur1.getCartesWagon(),
                CouleurWagon.ROUGE,CouleurWagon.LOCOMOTIVE,CouleurWagon.VERT));
        assertTrue(TestUtils.contientExactement(
                carteVisible,
                CouleurWagon.JAUNE,CouleurWagon.LOCOMOTIVE,CouleurWagon.VERT,CouleurWagon.ROSE, CouleurWagon.BLEU));
        assertTrue(TestUtils.contientExactement(
                pileWagon,
                CouleurWagon.LOCOMOTIVE, CouleurWagon.ORANGE));
        assertEquals(3,joueur1.getCartesWagon().size());
    }

    @Test
    void jouerTourConstruireDeuxGares() {
        List<CouleurWagon> cartesWagon = joueur3.getCartesWagon();
        cartesWagon.add(CouleurWagon.VERT);
        cartesWagon.add(CouleurWagon.BLEU);
        cartesWagon.add(CouleurWagon.BLEU);
        cartesWagon.add(CouleurWagon.ROUGE);
        cartesWagon.add(CouleurWagon.ROUGE);

        jeu.setInput("Paris", "ROUGE"); // premier tour, constuit une gare pour 1 carte
        joueur3.jouerTour();

        jeu.setInput("Madrid", "ROUGE", "BLEU", "BLEU"); // 2e tour, une gare pour 2 cartes
        joueur3.jouerTour();

        //assertEquals(joueur3, getVilleParNom("Madrid").getProprietaire());
        assertTrue(TestUtils.contientExactement(
                joueur3.getCartesWagon(),
                CouleurWagon.VERT, CouleurWagon.ROUGE));
        assertTrue(TestUtils.contientExactement(
                jeu.getDefausseCartesWagon(),
                CouleurWagon.ROUGE, CouleurWagon.BLEU, CouleurWagon.BLEU));
        assertEquals(1, joueur3.getNbGares());
    }

    @Test
    void jouerTourConstruireTroisGares() {
        List<CouleurWagon> cartesWagon = joueur3.getCartesWagon();
        cartesWagon.add(CouleurWagon.VERT);//
        cartesWagon.add(CouleurWagon.BLEU);//
        cartesWagon.add(CouleurWagon.BLEU);//
        cartesWagon.add(CouleurWagon.ROUGE);//
        cartesWagon.add(CouleurWagon.ROUGE);
        cartesWagon.add(CouleurWagon.VERT);//
        cartesWagon.add(CouleurWagon.VERT);//
        cartesWagon.add(CouleurWagon.BLEU);

        jeu.setInput("Paris", "ROUGE"); // premier tour, constuit une gare pour 1 carte
        joueur3.jouerTour();

        jeu.setInput("Madrid", "ROUGE", "BLEU", "BLEU"); // 2e tour, une gare pour 2 cartes
        joueur3.jouerTour();

        jeu.setInput("London", "VERT", "ROUGE", "VERT", "BLEU", "VERT"); // 3e tour, une gare pour 3 cartes
        joueur3.jouerTour();

        //assertEquals(joueur3, getVilleParNom("Paris").getProprietaire());
        //assertEquals(joueur3, getVilleParNom("Madrid").getProprietaire());
        //assertEquals(joueur3, getVilleParNom("London").getProprietaire());

        assertTrue(TestUtils.contientExactement(
                joueur3.getCartesWagon(),
                CouleurWagon.ROUGE, CouleurWagon.BLEU));
        assertTrue(TestUtils.contientExactement(
                jeu.getDefausseCartesWagon(),
                CouleurWagon.ROUGE, CouleurWagon.BLEU, CouleurWagon.BLEU, CouleurWagon.VERT, CouleurWagon.VERT, CouleurWagon.VERT));
        assertEquals(0, joueur3.getNbGares());
    }

    //@Disabled
    @Test
    public void testVerifierTrajet(){
        joueur1.getCartesWagon().clear();
        List<CouleurWagon> liste = joueur1.getCartesWagon();
        liste.add(CouleurWagon.VERT);
        liste.add(CouleurWagon.VERT);
        liste.add(CouleurWagon.JAUNE);
        liste.add(CouleurWagon.LOCOMOTIVE);
        liste.add(CouleurWagon.VERT);
        liste.add(CouleurWagon.JAUNE);
        liste.add(CouleurWagon.VERT);
        Ville v = new Ville("");
        Ville v1 = new Ville("");
        Route r = new Route(v, v1, 4, CouleurWagon.GRIS);
        assertEquals(CouleurWagon.VERT, r.trouveCouleurWagonQuandGris(joueur1));
        assertTrue(r.verifierTrajet(joueur1));
    }



    @Test
    void jouerTourConstruireQuatreGares() {
        List<CouleurWagon> cartesWagon = joueur3.getCartesWagon();
        cartesWagon.add(CouleurWagon.VERT);//
        cartesWagon.add(CouleurWagon.BLEU);//
        cartesWagon.add(CouleurWagon.BLEU);//
        cartesWagon.add(CouleurWagon.ROUGE);//
        cartesWagon.add(CouleurWagon.ROUGE);
        cartesWagon.add(CouleurWagon.VERT);//
        cartesWagon.add(CouleurWagon.VERT);//
        cartesWagon.add(CouleurWagon.ROSE);
        cartesWagon.add(CouleurWagon.ROSE);
        cartesWagon.add(CouleurWagon.ROSE);
        cartesWagon.add(CouleurWagon.ROSE);

        jeu.setInput("Paris", "ROUGE"); // premier tour, constuit une gare pour 1 carte
        joueur3.jouerTour();

        jeu.setInput("Madrid", "BLEU", "BLEU"); // 2e tour, une gare pour 2 cartes
        joueur3.jouerTour();

        jeu.setInput("London", "VERT", "VERT", "VERT"); // 3e tour, une gare pour 3 cartes
        joueur3.jouerTour();

        jeu.setInput("Berlin", "ROSE", "ROSE", "ROSE", "ROSE"); // 4e tour, une gare pour 4 cartes
        joueur3.jouerTour();

        assertEquals(joueur3, getVilleParNom("Paris").getProprietaire());
        assertEquals(joueur3, getVilleParNom("Madrid").getProprietaire());
        assertEquals(joueur3, getVilleParNom("London").getProprietaire());
        Assertions.assertNotEquals(joueur3, getVilleParNom("Berlin").getProprietaire());

        assertTrue(TestUtils.contientExactement(
                joueur3.getCartesWagon(),
                CouleurWagon.ROUGE, CouleurWagon.ROSE, CouleurWagon.ROSE, CouleurWagon.ROSE, CouleurWagon.ROSE));
        assertTrue(TestUtils.contientExactement(
                jeu.getDefausseCartesWagon(),
                CouleurWagon.ROUGE, CouleurWagon.BLEU, CouleurWagon.BLEU, CouleurWagon.VERT, CouleurWagon.VERT, CouleurWagon.VERT));
        assertEquals(0, joueur3.getNbGares());
    }

    @Test
    void jouerTourConstruireGaresLoco() {
        List<CouleurWagon> cartesWagon = joueur3.getCartesWagon();

        cartesWagon.add(CouleurWagon.LOCOMOTIVE);//

        cartesWagon.add(CouleurWagon.BLEU);//
        cartesWagon.add(CouleurWagon.LOCOMOTIVE);//

        cartesWagon.add(CouleurWagon.ROSE);//
        cartesWagon.add(CouleurWagon.LOCOMOTIVE);//
        cartesWagon.add(CouleurWagon.ROSE);//

        cartesWagon.add(CouleurWagon.ROUGE);



        jeu.setInput("Paris", "LOCOMOTIVE"); // premier tour, constuit une gare pour 1 carte
        joueur3.jouerTour();

        jeu.setInput("Madrid", "BLEU", "LOCOMOTIVE"); // 2e tour, une gare pour 2 cartes
        joueur3.jouerTour();

        jeu.setInput("Berlin", "ROSE", "LOCOMOTIVE", "ROSE"); // 3e tour, une gare pour 3 cartes
        joueur3.jouerTour();

        assertEquals(joueur3, getVilleParNom("Paris").getProprietaire());
        assertEquals(joueur3, getVilleParNom("Madrid").getProprietaire());
        assertEquals(joueur3, getVilleParNom("Berlin").getProprietaire());

        assertTrue(TestUtils.contientExactement(
                joueur3.getCartesWagon(),
                CouleurWagon.ROUGE));
        assertTrue(TestUtils.contientExactement(
                jeu.getDefausseCartesWagon(),
                CouleurWagon.LOCOMOTIVE, CouleurWagon.BLEU, CouleurWagon.LOCOMOTIVE, CouleurWagon.ROSE, CouleurWagon.ROSE, CouleurWagon.LOCOMOTIVE));
        assertEquals(0, joueur3.getNbGares());
    }

    @Test
    void testJouerTourCapturerFerry() {
        List<CouleurWagon> cartesWagon = joueur2.getCartesWagon();
        cartesWagon.add(CouleurWagon.BLEU);
        cartesWagon.add(CouleurWagon.BLEU);
        cartesWagon.add(CouleurWagon.ROUGE);
        cartesWagon.add(CouleurWagon.ROUGE);
        cartesWagon.add(CouleurWagon.LOCOMOTIVE);

        jeu.setInput(
                "Amsterdam - London", // coûte 2 Loco (ne peut pas capturer)
                "Dieppe - London(1)", // coûte 1 Loco+1 autre
                "BLEU", // ok
                "ROUGE", // ne convient pas pour une ferry avec 1 loco
                "LOCOMOTIVE" // ok
        );

        joueur2.jouerTour();
        assertEquals(null, getRouteParNom("Amsterdam - London").getProprietaire());
        assertEquals(joueur2, getRouteParNom("Dieppe - London(1)").getProprietaire());
        assertTrue(TestUtils.contientExactement(
                joueur2.getCartesWagon(),
                CouleurWagon.BLEU, CouleurWagon.ROUGE, CouleurWagon.ROUGE));
        assertTrue(TestUtils.contientExactement(
                jeu.getDefausseCartesWagon(),
                CouleurWagon.BLEU,
                CouleurWagon.LOCOMOTIVE));
    }

    @Test
    void testJouerTourCapturerFerryLongueur2Loco() {
        List<CouleurWagon> cartesWagon = joueur2.getCartesWagon();
        cartesWagon.add(CouleurWagon.BLEU);
        cartesWagon.add(CouleurWagon.BLEU);
        cartesWagon.add(CouleurWagon.ROUGE);
        cartesWagon.add(CouleurWagon.LOCOMOTIVE);
        cartesWagon.add(CouleurWagon.LOCOMOTIVE);

        jeu.setInput(
                "Amsterdam - London", // coûte 2 Loco (ne peut pas capturer)
                "BLEU", //  ne convient pas pour un ferry avec 2 loco
                "ROUGE", // ne convient pas pour un ferry avec 2 loco
                "LOCOMOTIVE",// ok
                "LOCOMOTIVE" // ok
        );

        joueur2.jouerTour();
        assertEquals(joueur2, getRouteParNom("Amsterdam - London").getProprietaire());
        assertTrue(TestUtils.contientExactement(
                joueur2.getCartesWagon(),
                CouleurWagon.BLEU, CouleurWagon.BLEU, CouleurWagon.ROUGE));
        assertTrue(TestUtils.contientExactement(
                jeu.getDefausseCartesWagon(),
                CouleurWagon.LOCOMOTIVE,
                CouleurWagon.LOCOMOTIVE));
    }

    @Test
    void testJouerTourCapturerFerryLongueur3() {
        List<CouleurWagon> cartesWagon = joueur2.getCartesWagon();
        cartesWagon.add(CouleurWagon.BLEU);
        cartesWagon.add(CouleurWagon.BLEU);
        cartesWagon.add(CouleurWagon.ROUGE);
        cartesWagon.add(CouleurWagon.ROUGE);
        cartesWagon.add(CouleurWagon.LOCOMOTIVE);

        jeu.setInput(
                "Essen - Kobenhavn(2)", // coûte 1 Loco et 2 Autres
                "BLEU", //  ok
                "ROUGE", // ne convient pas car on pose avec bleu
                "BLEU",// ok
                "LOCOMOTIVE" // ok
        );

        joueur2.jouerTour();
        assertEquals(joueur2, getRouteParNom("Essen - Kobenhavn(2)").getProprietaire());
        assertTrue(TestUtils.contientExactement(
                joueur2.getCartesWagon(),
                CouleurWagon.ROUGE, CouleurWagon.ROUGE));
        assertTrue(TestUtils.contientExactement(
                jeu.getDefausseCartesWagon(),
                CouleurWagon.BLEU,CouleurWagon.BLEU,
                CouleurWagon.LOCOMOTIVE));
    }

    @Disabled
    @Test
    public void testCarteValide(){
        List<CouleurWagon> liste = joueur1.getCartesWagon();
        liste.add(CouleurWagon.BLANC);
        liste.add(CouleurWagon.JAUNE);
        liste.add(CouleurWagon.LOCOMOTIVE);
        liste.add(CouleurWagon.LOCOMOTIVE);
        Ville v = new Ville("");
        Ville v1 = new Ville("");
        Route r = new Route(v, v1, 2, CouleurWagon.GRIS);
        assertTrue(joueur1.carteValide(CouleurWagon.JAUNE, r));
        assertTrue(joueur1.carteValide(CouleurWagon.BLANC, r));
        assertTrue(joueur1.carteValide(CouleurWagon.LOCOMOTIVE, r));
    }

    @Disabled
    @Test
    public void testCartesTri(){
        List<CouleurWagon> c = joueur3.getCartesWagonPosees();
        c.add(CouleurWagon.JAUNE);
        c.add(CouleurWagon.ROUGE);
        c.add(CouleurWagon.LOCOMOTIVE);
        c.add(CouleurWagon.LOCOMOTIVE);
        List<CouleurWagon> listeValide = new ArrayList<>();
        listeValide.add(CouleurWagon.JAUNE);
        listeValide.add(CouleurWagon.LOCOMOTIVE);
        listeValide.add(CouleurWagon.LOCOMOTIVE);
        Route r = new Route(new Ville(""), new Ville(""), 3, CouleurWagon.GRIS);
        joueur3.cartesPoseesTri(r);
        assertEquals(listeValide, joueur3.getCartesWagonPosees());
    }




    /*
        @Disabled
        @Test
        public void poser2gares(){
            joueur1.setNbGares(-1);
            List<CouleurWagon> cartes = joueur1.getCartesWagon();
            cartes.add(CouleurWagon.LOCOMOTIVE);
            cartes.add(CouleurWagon.BLANC);
            cartes.add(CouleurWagon.ROUGE);
            jeu.setInput("Fini");
            joueur1.jouerTour();
            assertEquals(joueur1.getNbGares(), 1);
        }

        @Test
        void testTrouverCouleurQuandGris(){
            List<CouleurWagon> CouleurPrises = joueur1.getCartesWagonPosees();
            CouleurPrises.add(CouleurWagon.LOCOMOTIVE);
            CouleurPrises.add(CouleurWagon.BLEU);
            CouleurPrises.add(CouleurWagon.LOCOMOTIVE);
            assertEquals(joueur1.trouveCouleurWagonQuandGris(), CouleurWagon.BLEU);
        }

        @Test
        void testVerifierTrajetCouleur(){
            List<CouleurWagon> CouleurPrises = joueur1.getCartesWagonPosees();
            CouleurPrises.add(CouleurWagon.LOCOMOTIVE);
            CouleurPrises.add(CouleurWagon.LOCOMOTIVE);
            CouleurPrises.add(CouleurWagon.ROSE);
            assertTrue(joueur1.verifierTrajetCouleur(CouleurWagon.ROSE, 3));
        }

        @Test
        void testVerifierTrajetCouleurFalse(){
            List<CouleurWagon> CouleurPrises = joueur1.getCartesWagonPosees();
            CouleurPrises.add(CouleurWagon.NOIR);
            CouleurPrises.add(CouleurWagon.ROSE);
            CouleurPrises.add(CouleurWagon.ROSE);
            assertFalse(joueur1.verifierTrajetCouleur(CouleurWagon.ROSE, 3));
        }
    */
    @Test
    void testVerifierTrain(){
        List<CouleurWagon> cartesDonnees = joueur2.getCartesWagon();
        cartesDonnees.add(CouleurWagon.JAUNE);
        cartesDonnees.add(CouleurWagon.JAUNE);
        Ville v1 = new Ville("BLBL");
        Ville v2 = new Ville("AAAA");
        Route r = new Route(v1, v2,2,CouleurWagon.JAUNE);
        assertTrue(r.verifierTrajet(joueur2));
    }


    @Test
    void testVerifierTrainFalse(){
        List <CouleurWagon> cartes = joueur3.getCartesWagon();
        cartes.add(CouleurWagon.ROUGE);
        cartes.add(CouleurWagon.JAUNE);
        Ville v = new Ville("Johto");
        Ville v1 = new Ville("Galar");
        //Forcément c'est false (il faut un bateau pas un train)
        Route r = new Route(v, v1, 2, CouleurWagon.JAUNE);
        assertFalse(r.verifierTrajet(joueur3));
    }

    @Test
    void testVerifierTrajetGris(){
        List<CouleurWagon> cartes = joueur1.getCartesWagon();
        cartes.add(CouleurWagon.ROUGE);
        cartes.add(CouleurWagon.ROUGE);
        cartes.add(CouleurWagon.LOCOMOTIVE);
        Ville v = new Ville("ChateauBerne");
        Ville v2 = new Ville("ChateauLutheran");
        Route r = new Route(v, v2, 3, CouleurWagon.GRIS);
        assertTrue(r.verifierTrajet(joueur1));
    }

    @Test
    void testVerifierTrajetLoco(){
        List<CouleurWagon> cartes = joueur1.getCartesWagon();
        cartes.add(CouleurWagon.LOCOMOTIVE);
        cartes.add(CouleurWagon.LOCOMOTIVE);
        cartes.add(CouleurWagon.LOCOMOTIVE);
        Ville v = new Ville("Assent");
        Ville v2 = new Ville("Split");
        Route r = new Route(v, v2, 3, CouleurWagon.ROUGE);
        assertTrue(r.verifierTrajet(joueur1));
    }

    @Test
    void testVerifierTunnel(){
        Ville v = new Ville("Ales");
        Ville v1 = new Ville("Millau");
        Tunnel t = new Tunnel(v, v1, 2, CouleurWagon.BLANC);
        List<CouleurWagon> cartes = joueur1.getCartesWagon();
        cartes.add(CouleurWagon.LOCOMOTIVE);
        cartes.add(CouleurWagon.LOCOMOTIVE);
        assertTrue(t.verifierTrajet(joueur1));
    }

    @Test
    void testVerifierTunnelFalse(){
        Ville v = new Ville("Tilted Tower");
        Ville v1 = new Ville("Salty Spring");
        Tunnel t = new Tunnel(v, v1, 3, CouleurWagon.GRIS);
        List<CouleurWagon> cartes = joueur3.getCartesWagon();
        cartes.add(CouleurWagon.LOCOMOTIVE);
        cartes.add(CouleurWagon.ROUGE);
        cartes.add(CouleurWagon.JAUNE);
        assertFalse(t.verifierTrajet(joueur3));
    }

    @Test
    void testVerifierFerry(){
        Ville v = new Ville("SpringField");
        Ville v1 = new Ville("Fatal Field");
        Ferry f = new Ferry(v, v1, 2, CouleurWagon.GRIS, 1);
        List <CouleurWagon> carte = joueur4.getCartesWagon();
        carte.add(CouleurWagon.LOCOMOTIVE);
        carte.add(CouleurWagon.LOCOMOTIVE);
        assertTrue(f.verifierTrajet(joueur4));
    }

    @Test
    void testVerifierFerryFalse(){
        Ville v = new Ville("SoutheParque");
        Ville v1 = new Ville("Tomato Town");
        Ferry f = new Ferry(v1, v, 4, CouleurWagon.BLANC, 2);
        List<CouleurWagon> cartes = joueur2.getCartesWagon();
        cartes.add(CouleurWagon.ROUGE);
        cartes.add(CouleurWagon.LOCOMOTIVE);
        cartes.add(CouleurWagon.LOCOMOTIVE);
        cartes.add(CouleurWagon.LOCOMOTIVE);
        assertFalse(f.verifierTrajet(joueur2));
    }

    /*
    @Disabled
    @Test
    public void testFerrySansLoco(){
        Ferry f = new Ferry(new Ville(""), new Ville(""), 2, CouleurWagon.GRIS, 1);
        List<CouleurWagon> liste = joueur3.getCartesWagon();

        assertFalse(f.capturerRoute(joueur3));
    }
     */
    @Disabled
    @Test
    public void testPoserGare(){
        List<CouleurWagon> liste = joueur2.getCartesWagon();
        liste.add(CouleurWagon.BLANC);
        jeu.setInput("Paris", "BLANC");
        joueur2.jouerTour();
        assertEquals(2, joueur2.getNbGares());
    }

    @Disabled
    @Test
    public void testPoserTunnel(){
        jeu.setInput("Angora - Constantinople", "BLEU", "BLEU", "BLEU");
        joueur2.getCartesWagon().add(CouleurWagon.BLEU);
        joueur2.getCartesWagon().add(CouleurWagon.BLEU);
        joueur2.getCartesWagon().add(CouleurWagon.BLEU);
        jeu.getPileCartesWagon().add(0, CouleurWagon.BLEU);
        jeu.getPileCartesWagon().add(0, CouleurWagon.JAUNE);
        jeu.getPileCartesWagon().add(0, CouleurWagon.ROSE);
        joueur2.jouerTour();
        List<CouleurWagon> liste = new ArrayList<>();
        assertEquals(joueur2.getCartesWagon(), liste);
        //assertTrue(getRouteParNom("Angora - Constantinople").getProprietaire() == joueur2);
    }


}
