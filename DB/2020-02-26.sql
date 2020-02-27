update app_config set config_value='<p style="color: #000;">Hi!</p>
<p style="color: #000;">You have received an automated workflow alert for %TENANT%. Click on <a href="%GRANT_LINK%">%GRANT_NAME%</a> to review.</p>
<p>&nbsp;</p>
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
</table>
<p>&nbsp;</p>
<hr />
<p style="text-align: center; color: #000;">This is an automatically generated email. Please do not reply to this message.</p>
<hr />
<p style="text-align: center; color: #000;"><strong>Anudan &ndash; A simple Grant Management tool.</strong></p>
<p style="text-align: center; color: #000;">&copy; 2020 Foundation for Innovation and Social Entrepreneurship. All rights reserved.</p>
<p style="text-align: center; color: #000;">Social Alpha | India | <a href="https://www.socialalpha.org">www.socialalpha.org</a></p>' where config_name='GRANT_STATE_CHANGED_MAIL_MESSAGE';

update app_config set config_value='<hr />
<span style="color: #808080;"><em>The content of this message is confidential. If you have received it by mistake, please inform us by an email reply and then delete the message. It is forbidden to copy, forward, or in any way reveal the contents of this message to anyone. The integrity and security of this email cannot be guaranteed over the Internet. Therefore, the sender will not be held liable for any damage caused by the message.</em></span></p>
<hr />
<p>&nbsp;</p>' where config_name='PLATFORM_EMAIL_FOOTER';

update app_config set config_value='Workflow Alert | Status of %GRANT_NAME% has changed.' where config_name='GRANT_STATE_CHANGED_MAIL_SUBJECT';