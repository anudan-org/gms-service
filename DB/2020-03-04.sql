insert into app_config (config_name,config_value) values('GRANT_STATE_CHANGED_NOTIFICATION_SUBJECT','Workflow Alert | Status of %GRANT_NAME% has changed.');
insert into app_config (config_name,config_value) values('GRANT_STATE_CHANGED_NOTIFICATION_MESSAGE','
<p style="color: #000;"><strong>Change Summary: </strong></p>
<hr />
<table border="1" style="border-color:#fafafa;" width="100%" cellspacing="0" cellpadding="2">
<tbody>
<tr>
<td width="25%">
<p style="font-size: 11px; color: #000;margin:0;">Name of the Grant:</p>
</td>
<td><span style="font-size: 14px; color: #000; font-weight: bold;">%GRANT_NAME%</span></td>
</tr>
<tr>
<td>
<p style="font-size: 11px; color: #000;margin:0;">Current State:</p>
</td>
<td><span style="font-size: 14px; color: #00b050; font-weight: bold;">%CURRENT_STATE%</span></td>
</tr>
<tr>
<td>
<p style="font-size: 11px; color: #000;margin:0;">State Owner:</p>
</td>
<td><span style="font-size: 14px; color: #00b050; font-weight: bold;">%CURRENT_OWNER%</span></td>
</tr>
</tbody>
</table>
<br>
<table border="1" style="border-color:#fafafa;" width="100%" cellspacing="0" cellpadding="2">
<tbody>
<tr>
<td width="25%">
<p style="font-size: 11px; color: #000;margin:0;">Previous State:</p>
</td>
<td><span style="font-size: 14px; color: #7f7f7f; font-weight: bold;">%PREVIOUS_STATE%</span></td>
</tr>
<tr>
<td width="25%">
<p style="font-size: 11px; color: #000;margin:0;">Previous State Owner:</p>
</td>
<td><span style="font-size: 14px; color: #7f7f7f; font-weight: bold;">%PREVIOUS_OWNER%</span></td>
</tr>
<tr>
<td width="25%">
<p style="font-size: 11px; color: #000;margin:0;">Previous Action:</p>
</td>
<td><span style="font-size: 14px; color: #7f7f7f; font-weight: bold;">%PREVIOUS_ACTION%</span></td>
</tr>
</tbody>
</table>
<p>&nbsp;</p>
<table border="1" style="border-color:#fafafa;" width="100%" cellspacing="0" cellpadding="2">
<tbody>
<tr>
<td width="25%">
<p style="font-size: 11px; color: #000;margin:0;">Changes from the previous state to the current state:</p>
</td>
<td width="70%"><span style="font-size: 14px; color: #00b050; font-weight: bold;">%HAS_CHANGES%. %HAS_CHANGES_COMMENT%</span></td>
</tr>
<tr>
<td width="25%">
<p style="font-size: 11px; color: #000;margin:0;">Notes attached to state change:</p>
</td>
<td width="70%"><span style="font-size: 14px; color: #00b050; font-weight: bold;">%HAS_NOTES%. %HAS_NOTES_COMMENT%</span></td>
</tr>
</tbody>
</table>');

insert into app_config (config_name,config_value) values('REPORT_STATE_CHANGED_NOTIFICATION_SUBJECT','Workflow Alert | Status of %REPORT_NAME% has changed.');
insert into app_config (config_name,config_value) values('REPORT_STATE_CHANGED_NOTIFICATION_MESSAGE','
<p style="color: #000;"><strong>Change Summary: </strong></p>
<hr />
<table border="1" style="border-color:#fafafa;" width="100%" cellspacing="0" cellpadding="2">
<tbody>
<tr>
<td width="25%">
<p style="font-size: 11px; color: #000;margin:0;">Name of the Report:</p>
</td>
  <td><span style="font-size: 14px; color: #000; font-weight: bold;">%REPORT_NAME% <span style="font-size: 14px; color: #000; font-weight: normal;">for Grant "%GRANT_NAME%"</span> </span></td>
</tr>
<tr>
<td>
<p style="font-size: 11px; color: #000;margin:0;">Current State:</p>
</td>
<td><span style="font-size: 14px; color: #00b050; font-weight: bold;">%CURRENT_STATE%</span></td>
</tr>
<tr>
<td>
<p style="font-size: 11px; color: #000;margin:0;">State Owner:</p>
</td>
<td><span style="font-size: 14px; color: #00b050; font-weight: bold;">%CURRENT_OWNER%</span></td>
</tr>
</tbody>
</table>
<br>
<table border="1" style="border-color:#fafafa;" width="100%" cellspacing="0" cellpadding="2">
<tbody>
<tr>
<td width="25%">
<p style="font-size: 11px; color: #000;margin:0;">Previous State:</p>
</td>
<td><span style="font-size: 14px; color: #7f7f7f; font-weight: bold;">%PREVIOUS_STATE%</span></td>
</tr>
<tr>
<td width="25%">
<p style="font-size: 11px; color: #000;margin:0;">Previous State Owner:</p>
</td>
<td><span style="font-size: 14px; color: #7f7f7f; font-weight: bold;">%PREVIOUS_OWNER%</span></td>
</tr>
<tr>
<td width="25%">
<p style="font-size: 11px; color: #000;margin:0;">Previous Action:</p>
</td>
<td><span style="font-size: 14px; color: #7f7f7f; font-weight: bold;">%PREVIOUS_ACTION%</span></td>
</tr>
</tbody>
</table>
<p>&nbsp;</p>
<table border="1" style="border-color:#fafafa;" width="100%" cellspacing="0" cellpadding="2">
<tbody>
<tr>
<td width="25%">
<p style="font-size: 11px; color: #000;margin:0;">Changes from the previous state to the current state:</p>
</td>
<td width="70%"><span style="font-size: 14px; color: #00b050; font-weight: bold;">%HAS_CHANGES%. %HAS_CHANGES_COMMENT%</span></td>
</tr>
<tr>
<td width="25%">
<p style="font-size: 11px; color: #000;margin:0;">Notes attached to state change:</p>
</td>
<td width="70%"><span style="font-size: 14px; color: #00b050; font-weight: bold;">%HAS_NOTES%. %HAS_NOTES_COMMENT%</span></td>
</tr>
</tbody>
</table>');

alter table notifications add column report_id bigint,add column notification_for varchar(25);
delete from notifications;
