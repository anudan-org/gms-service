update app_config set config_value='"%GRANT_NAME%" has changed.<br><br>


Current State = <b>%CURRENT_STATE%</b> | Current Owner = <b>%CURRENT_OWNER%</b><br>
Previous State = <b>%PREVIOUS_STATE%</b> | Previous Owner = <b>%PREVIOUS_OWNER%</b> | Previous Action = <b>%PREVIOUS_ACTION%</b><br><br>

Changes from previous state to current state = <b>%HAS_CHANGES%</b>. <b>%HAS_CHANGES_COMMENT%</b><br>
Notes attached with state change = <b>%HAS_NOTES%</b>. <b>%HAS_NOTES_COMMENT%</b><br>' where config_name='GRANT_STATE_CHANGED_MAIL_MESSAGE';

insert into app_config (config_name,config_value) values('PLATFORM_EMAIL_FOOTER','<hr />
<p>&copy; 2019 Foundation for Innovation and Social Entrepreneurship</p>
<p>Social Alpha | India |&nbsp;<a href="https://www.socialalpha.org/">https://www.socialalpha.org/</a></p>
<p><br /><span style="color: #808080;"><em>The content of this message is confidential. If you have received it by mistake, please inform us by an email reply and then delete the message. It is forbidden to copy, forward, or in any way reveal the contents of this message to anyone. The integrity and security of this email cannot be guaranteed over the Internet. Therefore, the sender will not be held liable for any damage caused by the message.</em></span></p>
<hr />
<p>&nbsp;</p>');