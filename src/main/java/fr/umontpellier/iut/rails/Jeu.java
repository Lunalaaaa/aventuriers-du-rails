package fr.umontpellier.iut.rails;

import com.google.gson.Gson;
import fr.umontpellier.iut.gui.GameServer;

import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.Collectors;

public class Jeu implements Runnable {
    /**
     * Liste des joueurs
     */
    private List<Joueur> joueurs;

    /**
     * Le joueur dont c'est le tour
     */
    private Joueur joueurCourant;
    /**
     * Liste des villes représentées sur le plateau de jeu
     */
    private List<Ville> villes;
    /**
     * Liste des routes du plateau de jeu
     */
    private List<Route> routes;
    /**
     * Pile de pioche (face cachée)
     */
    private List<CouleurWagon> pileCartesWagon;
    /**
     * Cartes de la pioche face visible (normalement il y a 5 cartes face visible)
     */
    private List<CouleurWagon> cartesWagonVisibles;
    /**
     * Pile de cartes qui ont été défaussée au cours de la partie
     */
    private List<CouleurWagon> defausseCartesWagon;
    /**
     * Pile des cartes "Destination" (uniquement les destinations "courtes", les
     * destinations "longues" sont distribuées au début de la partie et ne peuvent
     * plus être piochées après)
     */
    private List<Destination> pileDestinations;
    /**
     * File d'attente des instructions recues par le serveur
     */
    private BlockingQueue<String> inputQueue;
    /**
     * Messages d'information du jeu
     */
    private List<String> log;

    /**
     * retourne un nombre aléatoire entre 0 et max
     */
    public int getRandomInt(int max) {
        Random random = new Random();
        int nb;
        nb = random.nextInt(max);
        return nb;
    }

    public Jeu(String[] nomJoueurs) {
        /*
         * ATTENTION : Cette méthode est à réécrire.
         *
         * Le code indiqué ici est un squelette minimum pour que le jeu se lance et que
         * l'interface graphique fonctionne.
         * Vous devez modifier ce code pour que les différents éléments du jeu soient
         * correctement initialisés.
         */

        // initialisation des entrées/sorties
        inputQueue = new LinkedBlockingQueue<>();
        log = new ArrayList<>();

        // création des cartes
        pileCartesWagon = pileWagonMelanger(makePileCarteWagon());
        cartesWagonVisibles = makePileCarteWagonVisible();
        defausseCartesWagon = new ArrayList<>();
        pileDestinations = pileDestinationMelanger(Destination.makeDestinationsEurope());

        // création des joueurs
        ArrayList<Joueur.Couleur> couleurs = new ArrayList<>(Arrays.asList(Joueur.Couleur.values()));
        Collections.shuffle(couleurs);
        joueurs = new ArrayList<>();
        for (String nom : nomJoueurs) {
            Joueur joueur = new Joueur(nom, this, couleurs.remove(0));
            joueurs.add(joueur);
        }
        joueurCourant = joueurs.get(0);

        // création des villes et des routes
        Plateau plateau = Plateau.makePlateauEurope();
        villes = plateau.getVilles();
        routes = plateau.getRoutes();
    }

    public List<CouleurWagon> getPileCartesWagon() {
        return pileCartesWagon;
    }

    public List<CouleurWagon> getCartesWagonVisibles() {
        return cartesWagonVisibles;
    }

    public List<Ville> getVilles() {
        return villes;
    }

    public List<Route> getRoutes() {
        return routes;
    }

    public Joueur getJoueurCourant() {
        return joueurCourant;
    }

    public List<CouleurWagon> getDefausseCartesWagon() {
        return defausseCartesWagon;
    }

    public List<Destination> getPileDestinations() {
        return pileDestinations;
    }

    /**
     * @return une liste avec tous les noms des cartes visibles
     */
    public ArrayList<String> getNomWagonVisible() {
        ArrayList<String> nomWagonVisible = new ArrayList<>();
        for (int i = 0; i < cartesWagonVisibles.size(); i++) {
            nomWagonVisible.add(cartesWagonVisibles.get(i).name());
        }
        return nomWagonVisible;
    }

    /**
     * @return une liste avec les noms des cartes visible sans les locomotives
     */
    public ArrayList<String> getNomWagonVisibleSimple() {
        ArrayList<String> nomWagonVisibleSimple = getNomWagonVisible();
        for (int i = 0; i < nomWagonVisibleSimple.size(); i++) {
            if (nomWagonVisibleSimple.get(i).equals(CouleurWagon.LOCOMOTIVE.name())) {
                nomWagonVisibleSimple.remove(i);
            }
        }
        return nomWagonVisibleSimple;
    }

    /**
     * Exécute la partie
     */
    public void run() {
        /*
         * ATTENTION : Cette méthode est à réécrire.
         *
         * Cette méthode doit :
         * - faire choisir à chaque joueur les destinations initiales qu'il souhaite
         * garder : on pioche 3 destinations "courtes" et 1 destination "longue", puis
         * le
         * joueur peut choisir des destinations à défausser ou passer s'il ne veut plus
         * en défausser. Il doit en garder au moins 2.
         * - exécuter la boucle principale du jeu qui fait jouer le tour de chaque
         * joueur à tour de rôle jusqu'à ce qu'un des joueurs n'ait plus que 2 wagons ou
         * moins
         * - exécuter encore un dernier tour de jeu pour chaque joueur après
         */
        //Distribution initiale des wagons et destinations
        for (int i = 0; i < joueurs.size(); i++) {
            joueurCourant = joueurs.get(i);
            joueurCourant.distributionInitialeWagon();
        }

        //Choix des destinations à défausser
        for (int i = 0; i < joueurs.size(); i++) {
            ArrayList<Destination> destinationsPossible=new ArrayList<>();
            joueurCourant = joueurs.get(i);
            for (int k=0;k<3;k++){
                destinationsPossible.add(piocherDestination());
            }
            destinationsPossible.add(joueurCourant.destinationLongue());
            List<Destination> destinationNonGarder = joueurCourant.choisirDestinations(destinationsPossible, 2);
            for (int j = 0; j < destinationNonGarder.size(); j++) {
                if (destinationNonGarder.get(j).getValeur()<20){
                    pileDestinations.add(destinationNonGarder.get(j));
                }
            }
        }

        //Début du jeu
        joueurCourant = joueurs.get(0);
        while (joueurCourant.getNbWagons() >= 2) {
            joueurCourant.jouerTour();
            if (joueurCourant.equals(joueurs.get(joueurs.size() - 1))) {
                joueurCourant = joueurs.get(0);
            } else {
                joueurCourant = joueurs.get(joueurs.indexOf(joueurCourant) + 1);
            }
        }

    }

    public Route getRouteParNom(String nom) {
        for (Route route : this.getRoutes()) {
            if (route.getNom().equals(nom)) {
                return route;
            }
        }
        return null;
    }

    /**
     * Ajoute une carte dans la pile de défausse.
     * Dans le cas peu probable, où il y a moins de 5 cartes wagon face visibles
     * (parce que la pioche
     * et la défausse sont vides), alors il faut immédiatement rendre cette carte
     * face visible.
     *
     * @param c carte à défausser
     */
    public void defausserCarteWagon(CouleurWagon c) {
            if (pileCartesWagon.isEmpty()) {
                if (cartesWagonVisibles.size() < 5) {
                    cartesWagonVisibles.add(c);
                }
            } 

        else {
            defausseCartesWagon.add(c);
        }
    }

    /**
     * Remet les destinations non garder dans la pile destination
     */
    public void defausserCarteDestination(List<Destination> d){
        for(int i=0;i<d.size();i++){
            pileDestinations.add(d.get(i));
        }
    }

    /**
     * Pioche une carte de la pile de pioche
     * Si la pile est vide, les cartes de la défausse sont replacées dans la pioche
     * puis mélangées avant de piocher une carte
     *
     * @return la carte qui a été piochée (ou null si aucune carte disponible)
     */

    public CouleurWagon piocherCarteWagon() {
        CouleurWagon carteWagonPiochee = null;
        if (pileCartesWagon.isEmpty()) {
            pileCartesWagon = pileWagonMelanger(defausseCartesWagon);
        } 
        if(!pileCartesWagon.isEmpty()){
            carteWagonPiochee = pileCartesWagon.get(0);
            pileCartesWagon.remove(0);
        }
        return carteWagonPiochee;
    }

    /**
     * tire 3 cartes pour le passage d'un tunnel retourne le nombre de cartes à rajouter
     */

    public int piocher3Cartes(CouleurWagon col){
        int nb = 0;
        this.log("Pioche de 3 cartes :");
        for(int i = 0; i < 3; i++){
            CouleurWagon cartePiochee = this.piocherCarteWagon();
            this.log(cartePiochee.toString());
            if(cartePiochee.equals(CouleurWagon.LOCOMOTIVE) || cartePiochee.equals(col)) {
                nb++;
            }
            defausseCartesWagon.add(cartePiochee);
        }
        this.log("Vous devez rajouter " + nb + " carte(s) pour passer ce tunnel.");
        return nb;
    }


    /**
     * Retire une carte wagon de la pile des cartes wagon visibles.
     * Si une carte a été retirée, la pile de cartes wagons visibles est recomplétée
     * (remise à 5, éventuellement remélangée si 3 locomotives visibles)
     */
    public void retirerCarteWagonVisible(CouleurWagon c) {
        int cpt = 0;
        cartesWagonVisibles.remove(c);
        while (cartesWagonVisibles.size() < 5) {
            cartesWagonVisibles.add(pileCartesWagon.get(0));
            pileCartesWagon.remove(0);
            for (int i = 0; i < cartesWagonVisibles.size(); i++) {
                if (cartesWagonVisibles.get(i).equals(CouleurWagon.LOCOMOTIVE)) {
                    cpt++;
                }
            }
            if (cpt >= 3) {
                for (int j = 0; j < 5; j++) {
                    cartesWagonVisibles.add(pileCartesWagon.get(0));
                    pileCartesWagon.remove(0);
                    cartesWagonVisibles.remove(j);
                }
            }
        }
    }

    /**
     * Pioche et renvoie la destination du dessus de la pile de destinations.
     *
     * @return la destination qui a été piochée (ou `null` si aucune destination
     * disponible)
     */
    public Destination piocherDestination() {
        Destination destinationPiocher;
        if (pileDestinations.isEmpty()) {
            destinationPiocher = null;
        } else {
            destinationPiocher = pileDestinations.get(0);
            pileDestinations.remove(0);
        }
        return destinationPiocher;
    }


    /**
     * @return la liste ordonné par couleur des wagons
     */
    public ArrayList<CouleurWagon> makePileCarteWagon() {
        ArrayList<CouleurWagon> pileWagon = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 12; j++) {
                pileWagon.add(CouleurWagon.getCouleursSimples().get(i));
            }
        }
        for (int k = 0; k < 14; k++) {
            pileWagon.add(CouleurWagon.LOCOMOTIVE);
        }
        return pileWagon;
    }


    public List<CouleurWagon> makePileCarteWagonVisible() {
        ArrayList<CouleurWagon> pileWagonVisible = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            pileWagonVisible.add(piocherCarteWagon());
        }
        return pileWagonVisible;
    }

    /**
     * @param pileWagonAvantMelange
     * @return la pile Wagon mélanger
     */
    public ArrayList<CouleurWagon> pileWagonMelanger(List<CouleurWagon> pileWagonAvantMelange) {
        int indiceAleat;
        ArrayList<CouleurWagon> temp = new ArrayList<>(pileWagonAvantMelange);
        ArrayList<CouleurWagon> pileWagonMelange = new ArrayList<>();
        while (temp.size() > 0) {
            indiceAleat = getRandomInt(temp.size());
            pileWagonMelange.add(temp.get(indiceAleat));
            temp.remove(temp.get(indiceAleat));
        }
        return pileWagonMelange;
    }

    /**
     * @return la  pile destination mélanger
     */
    public ArrayList<Destination> pileDestinationMelanger(ArrayList<Destination> pileDestinationAvantMelange) {
        int indexAleat;
        ArrayList<Destination> pileDestinationMelange = new ArrayList<Destination>();
        while (pileDestinationAvantMelange.size() > 0) {
            indexAleat = getRandomInt(pileDestinationAvantMelange.size());
            pileDestinationMelange.add(pileDestinationAvantMelange.get(indexAleat));
            pileDestinationAvantMelange.remove(pileDestinationAvantMelange.get(indexAleat));
        }
        return pileDestinationMelange;
    }


    public List<Joueur> getJoueurs() {
        return joueurs;
    }

    @Override
    public String toString() {
        StringJoiner joiner = new StringJoiner("\n");
        for (Joueur j : joueurs) {
            joiner.add(j.toString());
        }
        return joiner.toString();
    }

    /**
     * Ajoute un message au log du jeu
     */
    public void log(String message) {
        log.add(message);
    }

    /**
     * Ajoute un message à la file d'entrées
     */
    public void addInput(String message) {
        inputQueue.add(message);
    }

    /**
     * Lit une ligne de l'entrée standard
     * C'est cette méthode qui doit être appelée à chaque fois qu'on veut lire
     * l'entrée clavier de l'utilisateur (par exemple dans {@code Player.choisir})
     *
     * @return une chaîne de caractères correspondant à l'entrée suivante dans la
     * file
     */
    public String lireLigne() {
        try {
            return inputQueue.take();
        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Envoie l'état de la partie pour affichage aux joueurs avant de faire un choix
     *
     * @param instruction l'instruction qui est donnée au joueur
     * @param boutons     labels des choix proposés s'il y en a
     * @param peutPasser  indique si le joueur peut passer sans faire de choix
     */
    public void prompt(String instruction, Collection<String> boutons, boolean peutPasser) {
        System.out.println();
        System.out.println(this);
        if (boutons.isEmpty()) {
            System.out.printf(">>> %s: %s <<<%n", joueurCourant.getNom(), instruction);
        } else {
            StringJoiner joiner = new StringJoiner(" / ");
            for (String bouton : boutons) {
                joiner.add(bouton);
            }
            System.out.printf(">>> %s: %s [%s] <<<%n", joueurCourant.getNom(), instruction, joiner);
        }

        Map<String, Object> data = Map.ofEntries(
                new AbstractMap.SimpleEntry<String, Object>("prompt", Map.ofEntries(
                        new AbstractMap.SimpleEntry<String, Object>("instruction", instruction),
                        new AbstractMap.SimpleEntry<String, Object>("boutons", boutons),
                        new AbstractMap.SimpleEntry<String, Object>("nomJoueurCourant", getJoueurCourant().getNom()),
                        new AbstractMap.SimpleEntry<String, Object>("peutPasser", peutPasser))),
                new AbstractMap.SimpleEntry<>("villes",
                        villes.stream().map(Ville::asPOJO).collect(Collectors.toList())),
                new AbstractMap.SimpleEntry<>("routes",
                        routes.stream().map(Route::asPOJO).collect(Collectors.toList())),
                new AbstractMap.SimpleEntry<String, Object>("joueurs",
                        joueurs.stream().map(Joueur::asPOJO).collect(Collectors.toList())),
                new AbstractMap.SimpleEntry<String, Object>("piles", Map.ofEntries(
                        new AbstractMap.SimpleEntry<String, Object>("pileCartesWagon", pileCartesWagon.size()),
                        new AbstractMap.SimpleEntry<String, Object>("pileDestinations", pileDestinations.size()),
                        new AbstractMap.SimpleEntry<String, Object>("defausseCartesWagon", defausseCartesWagon),
                        new AbstractMap.SimpleEntry<String, Object>("cartesWagonVisibles", cartesWagonVisibles))),
                new AbstractMap.SimpleEntry<String, Object>("log", log));
        GameServer.setEtatJeu(new Gson().toJson(data));
    }
}
