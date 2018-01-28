package entity;

import com.sun.istack.internal.NotNull;

import javax.persistence.*;

@Entity
public class Note extends BaseEntity {

    @Id
    @GeneratedValue
    @Column(nullable = false, unique = true)
    private Integer id;

    @ManyToOne(targetEntity = Etudiant.class, fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    @NotNull
    private Etudiant etudiant;

    @ManyToOne(targetEntity = Module.class, fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    @NotNull
    private Module module;

    @Column(nullable = false)
    @NotNull
    private float valeur = 0;

    public Note() { }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Etudiant getEtudiant() {
        return etudiant;
    }

    public void setEtudiant(Etudiant etudiant) {
        this.etudiant = etudiant;
    }

    public Module getModule() {
        return module;
    }

    public void setModule(Module module) {
        this.module = module;
    }

    public float getValeur() {
        return valeur;
    }

    public void setValeur(float valeur) {
        this.valeur = valeur;
    }
}
