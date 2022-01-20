update app_config set config_value='<hr />
                                    <p style="text-align: center; color: #000;">%TENANT%</p>
                                    <p style="text-align: center; color: #000;"><strong>Anudan &ndash; A simple Grant Management tool.</strong></p>
                                    <p style="text-align: center; color: #000; font-size: 12px;"><em>%RELEASE_VERSION%</em></p>
                                    <p style="text-align: center; color: #000;">&copy;2022. All rights reserved.</p>
                                    <p style="text-align: center; color: #000;">Code Alpha | India | <a href="https://code-alpha.org/">www.code-alpha.org</a></p>
                                    <hr />
                                    <p><span style="color: #808080;"><em>The content of this message is confidential. If you have received it by mistake, please inform us by writing to&nbsp;admin@anudan.org and then delete the message. It is forbidden to copy, forward, or in any way reveal the contents of this message to anyone. The integrity and security of this email cannot be guaranteed over the Internet. Therefore, the sender will not be held liable for any damage caused by the message.</em></span></p>
                                    <hr />' where config_name='PLATFORM_EMAIL_FOOTER';