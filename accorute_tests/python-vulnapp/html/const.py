error_pattern = "%error_message%"
error_message = "<p><strong><font color=\"red\">%s</font></strong></p>"

notice_pattern = "%notice_message%"
notice_message = "<p><strong><font color=\"blue\">%s</font></strong></p>"

html_template = """
                        <html>
                                <head>
                                <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
                                <meta HTTP-EQUIV="Pragma" content="no-cache">
                                <meta HTTP-EQUIV="Expires" content="-1">
                                </head>


                                <body>
				<table width=100% height=100% border=0 cellspacing=6 cellpadding=0 >
				<tr>
					<td colspan="3" height=136 style="border: 1 solid #294B96">
					<center><h2>Демонстрационное web-приложение</h2></center>
					</td>
				</tr>
				<tr>
				  	<td >
					<table width=200 height=100% cellPadding=0 valign="top" align=center border=0>
					<tr valign="top">
  						<td align=right width=50%>
		  				<TABLE cellSpacing=4 cellPadding=6 border=0 valign="top" height=100%>
                  				<tr>
							<TD>
							&nbsp;&nbsp;<A href="news.html">Latest News</A>&nbsp;&nbsp;
                    					</TD>
						</tr>
                  				<tr>
							<TD>
							&nbsp;&nbsp;<A href="calculator.html">Calculator</A>&nbsp;&nbsp;
                    					</TD>
                  				</tr>
                  				<tr>
							<TD>
							&nbsp;&nbsp;<A href="statistics.html">Statistics</A>&nbsp;&nbsp;
                    					</TD>
                  				</tr>
                  				<tr>
							<TD>
							<br><br>
							&nbsp;&nbsp;<A href="login.html">Log in</A>&nbsp;&nbsp;
                    					</TD>
						</tr>
                  				<tr>
							<TD>
							&nbsp;&nbsp;<A href="editnews.html">Edit News</A>&nbsp;&nbsp;
                    					</TD>
						</tr>
                  				<tr>
							<TD>
							&nbsp;&nbsp;<A href="deletenews.html">Delete News</A>&nbsp;&nbsp;
                    					</TD>
						</tr>
                  				<tr>
							<TD>
							&nbsp;&nbsp;<A href="addnews.html">Post News</A>&nbsp;&nbsp;
                    					</TD>
						</tr>
                  				<tr>
							<TD>
							&nbsp;&nbsp;<A href="files.html">Manage Files</A>&nbsp;&nbsp;
                    					</TD>
						</tr>
                  				<tr>
							<TD>
							&nbsp;&nbsp;<A href="viewprofile.html">Profile</A>&nbsp;&nbsp;
                    					</TD>
						</tr>
                  				<tr>
							<TD>
							&nbsp;&nbsp;<A href="logout.html">Logout</A>&nbsp;&nbsp;
                    					</TD>
						</tr>
                  				<tr height="100%">
							<td height="100%">
							<img src="../resources/none.gif" width=1 height=100%><br>
							&nbsp;&nbsp; 
							</td>
						</tr>
						</table>
						</td>
					</tr>
					</table>
					</td>
					<td width="100%">
   					<TABLE cellSpacing=7 cellPadding=0 width=100% height=100% align=center style="border: 1 solid #294B96">
              				<TR height=100% valign=top>
			                <TD width=100% height=100%>
					%notice_message%
               				%error_message% 
					%contents%

					<img src="../resources/none.gif" width=100% height=1><br> 
					</TD>
					</TR>
					</TABLE>
					</td>
					</tr>
				</table>
                                </body>
                        </html>"""

