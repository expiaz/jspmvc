package entity;

import com.sun.istack.internal.NotNull;
import core.utils.Encoder;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
public class Etudiant extends BaseEntity implements User {

    @Id
    @GeneratedValue
    @Column(nullable = false, unique = true)
	private Integer id;

    @Column(nullable = false)
	private String prenom;

    @Column(nullable = false)
	private String nom;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    @NotNull
    private int absences = 0;

    @ManyToMany(cascade = {
        CascadeType.PERSIST,
        CascadeType.MERGE
    })
    @JoinTable
    private Set<Module> modules;

    @OneToMany(targetEntity = Note.class, mappedBy = "etudiant", fetch = FetchType.LAZY, cascade = {CascadeType.ALL})
    private Set<Note> notes;

	public Etudiant(){ }
	
	public Etudiant(String nom, String prenom) {
		this.prenom = prenom;
		this.nom = nom;
	}

	@Override
	public Integer getId() {
		return id;
	}

    @Override
	public void setId(Integer id) {
		this.id = id;
	}

	public String getPrenom() {
		return prenom;
	}

	public void setPrenom(String prenom) {
		this.prenom = prenom;
	}

	public String getNom() {
		return nom;
	}

	public void setNom(String nom) {
		this.nom = nom;
	}

    public int getAbsences() {
        return absences;
    }

    public void setAbsences(int absences) {
        this.absences = absences;
    }

    public Set<Note> getNotes() {
        return notes;
    }

    public Set<Note> getNotes(Module module) {
	    Set<Note> notes = new HashSet<>();
	    for (Note note : this.getNotes()) {
	        if (note.getModule().equals(module.getId())) {
	            notes.add(note);
            }
        }
        return notes;
    }

    public void addNote(Note note) {
        this.getNotes().add(note);
    }

    public void setNotes(Set<Note> notes) {
        this.notes = notes;
    }

    public Set<Module> getModules() {
        return modules;
    }

    public void setModules(Set<Module> modules) {
        this.modules = modules;
    }

    public void addModule(Module module) {
	    this.getModules().add(module);
    }

    public void removeModule(Module module) {
	    this.modules.remove(module);
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public void setLogin(String login) {
        this.setEmail(login);
    }

    @Override
    public String getLogin() {
        return this.getEmail();
    }

    @Override
    public void setPassword(String password) {
        this.password = Encoder.md5(password);
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public boolean isAdmin() {
        return false;
    }
}
