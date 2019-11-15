package ca.bc.gov.educ.api.digitalID.model;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import java.util.Date;

@Data
@Entity
@Table(name = "identity_type_code")
public class IdentityTypeCodeEntity {

    @Id
    @Column(name = "identity_type_code")
    String identityTypeCode;

    String label;

    String description;

    @NotNull(message="displayOrder cannot be null")
    @Column(name = "display_order")
    int displayOrder;

    @Column(name = "create_user", updatable = false)
    String createUser;

    @PastOrPresent
    @Column(name = "create_date", updatable = false)
    Date createDate;

    @Column(name = "update_user", updatable = false)
    String updateUser;

    @PastOrPresent
    @Column(name = "update_date", updatable = false)
    Date updateDate;

    @Column(name = "effective_date")
    Date effectiveDate;

    @Column(name = "expiry_date")
    Date expiry_date;
}
