update app_config set config_value='<p style="color: #000;">You have received an automated workflow alert for %TENANT%. Click the appropriate link below to&nbsp;review.</p> <p style="color: #000;">%GRANTEE% user: <a class="go-to-report-class" href="%GRANTEE_REPORT_LINK%">Click here</a></p> <p style="color: #000;">%GRANTER% user:&nbsp;<a class="go-to-report-class" href="%GRANTER_REPORT_LINK%">Click here</a></p> <p style="color: #000;">&nbsp;</p> <p>Report workflow status changed for <strong>%GRANTEE%</strong></p> <hr /> <p style="color: #000;"><strong>Change Summary:</strong></p> <table style="border-color: #fafafa;" border="1" width="100%" cellspacing="0" cellpadding="2"> <tbody> <tr> <td width="25%"> <p style="font-size: 11px; color: #000; margin: 0;">Name of the Report:</p> </td> <td><span style="font-size: 14px; color: #000; font-weight: bold;">%REPORT_NAME% <span style="font-size: 14px; color: #000; font-weight: normal;">for Grant "%GRANT_NAME%"</span> </span></td> </tr> <tr> <td> <p style="font-size: 11px; color: #000; margin: 0;">Current State:</p> </td> <td><span style="font-size: 14px; color: #00b050; font-weight: bold;">%CURRENT_STATE%</span></td> </tr> <tr> <td> <p style="font-size: 11px; color: #000; margin: 0;">State Owner:</p> </td> <td><span style="font-size: 14px; color: #00b050; font-weight: bold;">%CURRENT_OWNER%</span></td> </tr> </tbody> </table> <p>&nbsp;</p> <table style="border-color: #fafafa;" border="1" width="100%" cellspacing="0" cellpadding="2"> <tbody> <tr> <td width="25%"> <p style="font-size: 11px; color: #000; margin: 0;">Previous State:</p> </td> <td><span style="font-size: 14px; color: #7f7f7f; font-weight: bold;">%PREVIOUS_STATE%</span></td> </tr> <tr> <td width="25%"> <p style="font-size: 11px; color: #000; margin: 0;">Previous State Owner:</p> </td> <td><span style="font-size: 14px; color: #7f7f7f; font-weight: bold;">%PREVIOUS_OWNER%</span></td> </tr> <tr> <td width="25%"> <p style="font-size: 11px; color: #000; margin: 0;">Previous Action:</p> </td> <td><span style="font-size: 14px; color: #7f7f7f; font-weight: bold;">%PREVIOUS_ACTION%</span></td> </tr> </tbody> </table> <p>&nbsp;</p> <table style="border-color: #fafafa;" border="1" width="100%" cellspacing="0" cellpadding="2"> <tbody> <tr> <td width="25%"> <p style="font-size: 11px; color: #000; margin: 0;">Changes from the previous state to the current state:</p> </td> <td width="70%"><span style="font-size: 14px; color: #00b050; font-weight: bold;">%HAS_CHANGES%. %HAS_CHANGES_COMMENT%</span></td> </tr> <tr> <td width="25%"> <p style="font-size: 11px; color: #000; margin: 0;">Notes attached to state change:</p> </td> <td width="70%"><span style="font-size: 14px; color: #00b050; font-weight: bold;">%HAS_NOTES%. %HAS_NOTES_COMMENT%</span></td> </tr> </tbody> </table> <p>&nbsp;</p> <p>This is an automatically generated email. Please do not reply to this message.</p>' where config_name='REPORT_STATE_CHANGED_MAIL_MESSAGE';
update app_config set config_value='<p style="color: #000;">You have received an automated workflow alert for %TENANT%. Click on <a class="go-to-disbursement-class" href="%DISBURSEMENT_LINK%">Approval Request Note for %GRANT_NAME%</a> to review.</p> <p>Disbursement Approval Request workflow status changed for <strong>%GRANTEE%</strong></p> <p style="color: #000;"><strong>Change Summary: </strong></p> <table style="border-color: #fafafa;" border="1" width="100%" cellspacing="0" cellpadding="2"> <tbody> <tr> <td width="25%"> <p style="font-size: 11px; color: #000; margin: 0;">Approval Request for:</p> </td> <td><span style="font-size: 14px; color: #000; font-weight: bold;"><span style="font-size: 14px; color: #000; font-weight: normal;">"%GRANT_NAME%"</span> </span></td> </tr> <tr> <td> <p style="font-size: 11px; color: #000; margin: 0;">Current State:</p> </td> <td><span style="font-size: 14px; color: #00b050; font-weight: bold;">%CURRENT_STATE%</span></td> </tr> <tr> <td> <p style="font-size: 11px; color: #000; margin: 0;">State Owner:</p> </td> <td><span style="font-size: 14px; color: #00b050; font-weight: bold;">%CURRENT_OWNER%</span></td> </tr> </tbody> </table> <p>&nbsp;</p> <table style="border-color: #fafafa;" border="1" width="100%" cellspacing="0" cellpadding="2"> <tbody> <tr> <td width="25%"> <p style="font-size: 11px; color: #000; margin: 0;">Previous State:</p> </td> <td><span style="font-size: 14px; color: #7f7f7f; font-weight: bold;">%PREVIOUS_STATE%</span></td> </tr> <tr> <td width="25%"> <p style="font-size: 11px; color: #000; margin: 0;">Previous State Owner:</p> </td> <td><span style="font-size: 14px; color: #7f7f7f; font-weight: bold;">%PREVIOUS_OWNER%</span></td> </tr> <tr> <td width="25%"> <p style="font-size: 11px; color: #000; margin: 0;">Previous Action:</p> </td> <td><span style="font-size: 14px; color: #7f7f7f; font-weight: bold;">%PREVIOUS_ACTION%</span></td> </tr> </tbody> </table> <p>&nbsp;</p> <table style="border-color: #fafafa;" border="1" width="100%" cellspacing="0" cellpadding="2"> <tbody> <tr> <td width="25%"> <p style="font-size: 11px; color: #000; margin: 0;">Changes from the previous state to the current state:</p> </td> <td width="70%"><span style="font-size: 14px; color: #00b050; font-weight: bold;">%HAS_CHANGES%. %HAS_CHANGES_COMMENT%</span></td> </tr> <tr> <td width="25%"> <p style="font-size: 11px; color: #000; margin: 0;">Notes attached to state change:</p> </td> <td width="70%"><span style="font-size: 14px; color: #00b050; font-weight: bold;">%HAS_NOTES%. %HAS_NOTES_COMMENT%</span></td> </tr> </tbody> </table> <p>&nbsp;</p> <p>This is an automatically generated email. Please do not reply to this message.</p>' where config_name='DISBURSEMENT_STATE_CHANGED_MAIL_MESSAGE';
update app_config set config_value='<p style="color: #000;">You have received an automated workflow alert for %TENANT%. Click on <a class="go-to-grant-class" href="%GRANT_LINK%">%GRANT_NAME%</a> to review.</p> <p>Gant workflow status changed for <strong>%GRANTEE%</strong></p> <hr /> <p style="color: #000;"><strong>Change Summary: </strong></p> <table style="border-color: #fafafa;" border="1" width="100%" cellspacing="0" cellpadding="2"> <tbody> <tr> <td width="25%"> <p style="font-size: 11px; color: #000; margin: 0;">Name of the Grant:</p> </td> <td><span style="font-size: 14px; color: #000; font-weight: bold;">%GRANT_NAME%</span></td> </tr> <tr> <td> <p style="font-size: 11px; color: #000; margin: 0;">Current State:</p> </td> <td><span style="font-size: 14px; color: #00b050; font-weight: bold;">%CURRENT_STATE%</span></td> </tr> <tr> <td> <p style="font-size: 11px; color: #000; margin: 0;">State Owner:</p> </td> <td><span style="font-size: 14px; color: #00b050; font-weight: bold;">%CURRENT_OWNER%</span></td> </tr> </tbody> </table> <p>&nbsp;</p> <table style="border-color: #fafafa;" border="1" width="100%" cellspacing="0" cellpadding="2"> <tbody> <tr> <td width="25%"> <p style="font-size: 11px; color: #000; margin: 0;">Previous State:</p> </td> <td><span style="font-size: 14px; color: #7f7f7f; font-weight: bold;">%PREVIOUS_STATE%</span></td> </tr> <tr> <td width="25%"> <p style="font-size: 11px; color: #000; margin: 0;">Previous State Owner:</p> </td> <td><span style="font-size: 14px; color: #7f7f7f; font-weight: bold;">%PREVIOUS_OWNER%</span></td> </tr> <tr> <td width="25%"> <p style="font-size: 11px; color: #000; margin: 0;">Previous Action:</p> </td> <td><span style="font-size: 14px; color: #7f7f7f; font-weight: bold;">%PREVIOUS_ACTION%</span></td> </tr> </tbody> </table> <p>&nbsp;</p> <table style="border-color: #fafafa;" border="1" width="100%" cellspacing="0" cellpadding="2"> <tbody> <tr> <td width="25%"> <p style="font-size: 11px; color: #000; margin: 0;">Changes from the previous state to the current state:</p> </td> <td width="70%"><span style="font-size: 14px; color: #00b050; font-weight: bold;">%HAS_CHANGES%. %HAS_CHANGES_COMMENT%</span></td> </tr> <tr> <td width="25%"> <p style="font-size: 11px; color: #000; margin: 0;">Notes attached to state change:</p> </td> <td width="70%"><span style="font-size: 14px; color: #00b050; font-weight: bold;">%HAS_NOTES%. %HAS_NOTES_COMMENT%</span></td> </tr> </tbody> </table> <p>&nbsp;</p> <p>This is an automatically generated email. Please do not reply to this message.</p>' where config_name='GRANT_STATE_CHANGED_MAIL_MESSAGE';
