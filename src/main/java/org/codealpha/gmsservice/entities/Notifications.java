package org.codealpha.gmsservice.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Transient;
import org.codealpha.gmsservice.constants.GrantStatus;
import org.codealpha.gmsservice.models.GrantDetailVO;
import org.joda.time.DateTime;

@Entity
@Table(name = "notifications")
public class Notifications { 

	@Id
  	@GeneratedValue(strategy = GenerationType.IDENTITY)
  	@OrderBy("id ASC")
  	private Long id;

  	@Column
  	private String message;

  	@Column
  	private boolean read;

    @Column
    private Date postedOn;


  	@Column
  	private Long userId;

  	public void setMessage(String message){
  		this.message = message;
  	}

  	public String getMessage(){
  		return this.message;
  	}

  	public void setRead(boolean read){
  		this.read = read;
  	}

  	public boolean getRead(){
  		return this.read;
  	}

  	public void setUserId(Long userId){
  		this.userId = userId;
  	}

  	public Long getUserId(){
  		return this.userId;
  	}

    public void setPostedOn(Date dt){
      this.postedOn = dt;
    }

    public Date getPostedOn(){
      return this.postedOn;
    }
}