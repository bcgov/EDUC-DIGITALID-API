package ca.bc.gov.educ.api.digitalid.model;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;

@Getter
@Setter
@Entity
@Table(name = "access_channel_code")
public class AccessChannelCodeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "access_channel_code", unique = true, updatable = false)
	String accessChannelCode;

	@NotNull(message = "label cannot be null")
	@Column(name = "label")
	String label;

	@NotNull(message = "description cannot be null")
	@Column(name = "description")
	String description;

	@NotNull(message = "displayOrder cannot be null")
	@Column(name = "display_order")
	Integer displayOrder;

	@NotNull(message = "effectiveDate cannot be null")
	@Column(name = "effective_date")
	Date effectiveDate;

	@NotNull(message = "expiryDate cannot be null")
	@Column(name = "expiry_date")
	Date expiryDate;

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


}
