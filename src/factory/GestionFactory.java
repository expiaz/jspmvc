package factory;

import entity.Etudiant;

import java.util.*;

public class GestionFactory {

	/////// SIMULATION DE LA PERSISTANCE DES ETUDIANTS ET DES ABSENCES
	private static int ID = 0;
	private static Random rand = new Random();

	// CHARGER en premier à l'execution du projet (car constante : static final)
	private static final Map<Integer, Etudiant> LISTE_ID_ETUDIANTS = new HashMap<>();
	private static final Map<Integer, Integer> LISTE_ID_ABSENCES = new HashMap<>();
	private static final Map<Integer, List<Integer>> LISTE_ID_NOTES = new HashMap<>();


	static {
		save(create("Brunet-Manquat", "Francis"));
		save(create("Martin", "Philippe"));

		Map<Integer, Integer> listEtudiantAbsenceTemp = new HashMap<>();
		for (Etudiant etudiant : LISTE_ID_ETUDIANTS.values()) {
			listEtudiantAbsenceTemp.put(etudiant.getId(), rand.nextInt(10));
		}

		Map<Integer, List<Integer>> listNotesTemp = new HashMap<>();
		List<Integer> listTemp;
		for (Etudiant etudiant : LISTE_ID_ETUDIANTS.values()) {
			listTemp = new ArrayList<>();
			for(int i = 0; i < rand.nextInt(10); i++) {
				listTemp.add(rand.nextInt(20));
			}
			listNotesTemp.put(etudiant.getId(), listTemp);
		}
	}
	
	/////// METHODES A UTILISER
	// Retourne l'ensemble des etudiants
	public static Collection<Etudiant> getEtudiants() {
		return LISTE_ID_ETUDIANTS.values();
	}

	// Retourne un étudiant en fonction de son id 
	public static Etudiant getEtudiantById(int id) {
		return LISTE_ID_ETUDIANTS.get(id);
	}

	// Retourne le nombre d'absences d'un etudiant en fonction de son id 
	public static Integer getAbsencesByEtudiantId(int id) {
		return LISTE_ID_ABSENCES.get(id);
	}

	// Retourne les notes d'un etudiant en fonction de son id
	public static List<Integer> getNotesByEtudiantId(int id) {
		return LISTE_ID_NOTES.get(id);
	}


	public static Etudiant create(String nom, String prenom) {
		return new Etudiant(++ID, prenom, nom);
	}

	public static void save(Etudiant e) {
		LISTE_ID_ETUDIANTS.put(e.getId(), e);
	}

	public static void remove(Etudiant e){
		LISTE_ID_ETUDIANTS.remove(e.getId());
	}

}