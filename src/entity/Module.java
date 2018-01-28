package entity;

import javax.persistence.*;
import java.util.Set;

@Entity
public class Module extends BaseEntity {

    @Id
    @GeneratedValue
	@Column(nullable = false, unique = true)
	private Integer id;

    @Column(nullable = false, unique = true)
	private String nom;

    @ManyToMany(mappedBy = "modules", cascade = {CascadeType.MERGE, CascadeType.PERSIST}, fetch = FetchType.LAZY)
	private Set<Etudiant> etudiants;

	@OneToMany(targetEntity = Note.class, mappedBy = "module", fetch = FetchType.LAZY, cascade = {CascadeType.MERGE, CascadeType.PERSIST})
	private Set<Note> notes;

	public Module(){
	}

	public Module(String nom) {
		this.nom = nom;
	}

	@Override
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getNom() {
		return nom;
	}

	public void setNom(String nom) {
		this.nom = nom;
	}

	public Set<Etudiant> getEtudiants() {
		return etudiants;
	}

    public void setEtudiants(Set<Etudiant> etudiants) {
        this.etudiants = etudiants;
    }

    public void addEtudiant(Etudiant etudiant)
	{
		this.getEtudiants().add(etudiant);
	}

	public Set<Note> getNotes() {
		return notes;
	}

	public void addNote(Note note) {
        this.getNotes().add(note);
	}

	public void setNotes(Set<Note> notes) {
		this.notes = notes;
	}
}
