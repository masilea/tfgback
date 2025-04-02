package es.upm.dit.isst.tfgapi.model;

import java.util.List;
import java.util.stream.Collectors;

@Entity
public class Sesion {
    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Future private Date fecha;
    private String lugar;
    @Size(min = 3, max = 3)
    private List<@Email @NotEmpty String> tribunal;
    @JsonIgnore @OneToMany(mappedBy = "sesion")
    List<@Valid TFG> tfgs;

    //Constructor
    public Sesion(Long id, Date fecha, String lugar, List<@Email @NotEmpty String> tribunal, List<@Valid TFG> tfgs) {
        this.id = id;
        this.fecha = fecha;
        this.lugar = lugar;
        this.tribunal = tribunal;
        this.tfgs = tfgs;
    }

    //Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public String getLugar() {
        return lugar;
    }

    public void setLugar(String lugar) {
        this.lugar = lugar;
    }

    public List<String> getTribunal() {
        return tribunal;
    }

    public void setTribunal(List<String> tribunal) {
        this.tribunal = tribunal;
    }

    public List<TFG> getTfgs() {
        return tfgs;
    }

    @JsonGetter("tfgs")
    public String[] getEmailsTfgs() {
        if (tfgs != null) {
            return tfgs.stream().map(TFG::getAlumno).toArray(String[]::new);
        } else {
            return new String[0];
        }
    }

    @JsonProperty("tfgs")
    public void setTfgs(List<TFG> tfgs) {
        this.tfgs = tfgs;
    }

    
    //Hashcode
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((fecha == null) ? 0 : fecha.hashCode());
        result = prime * result + ((lugar == null) ? 0 : lugar.hashCode());
        result = prime * result + ((tribunal == null) ? 0 : tribunal.hashCode());
        result = prime * result + ((tfgs == null) ? 0 : tfgs.stream().map(TFG::getAlumno)
                                                      .collect(Collectors.toList())
                                                      .hashCode());
        return result;
    }
    
    //Equals
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Sesion other = (Sesion) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        if (fecha == null) {
            if (other.fecha != null)
                return false;
        } else if (!fecha.equals(other.fecha))
            return false;
        if (lugar == null) {
            if (other.lugar != null)
                return false;
        } else if (!lugar.equals(other.lugar))
            return false;
        if (tribunal == null) {
            if (other.tribunal != null)
                return false;
        } else if (!tribunal.equals(other.tribunal))
            return false;
        if (tfgs == null) {
            if (other.tfgs != null)
                return false;
        } else if (!tfgs.stream().map(TFG::getAlumno)
                            .collect(Collectors.toList())
                            .equals(other.tfgs.stream().map(TFG::getAlumno)
                                            .collect(Collectors.toList())))
            return false;
        return true;
    }
    
    //toString
    @Override
    public String toString() {
        return "Sesion [id=" + id + ", fecha=" + fecha + ", lugar=" + lugar 
            + ", tribunal=" + tribunal 
            + ", tfgs=" + (tfgs != null ? tfgs.stream().map(TFG::getAlumno)
                                          .collect(Collectors.toList()) 
                                      : "") + "]";
    }

}
