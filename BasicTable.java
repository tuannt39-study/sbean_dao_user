package mobi.softpay.pvi.common.beans;

import javax.persistence.*;
import java.io.Serializable;

@MappedSuperclass
public abstract class BasicTable
        implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID")
    @Access(AccessType.PROPERTY)
    protected Long id;
    @Version
    @Column(name = "VERSION")
    protected int version = 1;
    
    @Transient
    private Long rowCount;

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getVersion() {
        return this.version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public Long getRowCount() {
        return rowCount;
    }

    public void setRowCount(Long rowCount) {
        this.rowCount = rowCount;
    }
    
    
}
