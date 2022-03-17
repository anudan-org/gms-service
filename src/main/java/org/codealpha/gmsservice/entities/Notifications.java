package org.codealpha.gmsservice.entities;

import javax.persistence.*;
import java.util.Date;

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
	private String title;

  	@Column
  	private boolean read;

    @Column
    private Date postedOn;

    @Column
    private Long grantId;

    @Column
	private Long reportId;

	@Column
	private Long disbursementId;

    @Column
	private String notificationFor;

  	@Column
  	private Long userId;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

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


    public Long getGrantId() {
        return grantId;
    }

    public void setGrantId(Long grantId) {
        this.grantId = grantId;
    }

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public boolean isRead() {
		return read;
	}

	public Long getReportId() {
		return reportId;
	}

	public void setReportId(Long reportId) {
		this.reportId = reportId;
	}

	public String getNotificationFor() {
		return notificationFor;
	}

	public void setNotificationFor(String notificationFor) {
		this.notificationFor = notificationFor;
	}

	public Long getDisbursementId() {
		return disbursementId;
	}

	public void setDisbursementId(Long disbursementId) {
		this.disbursementId = disbursementId;
	}

}