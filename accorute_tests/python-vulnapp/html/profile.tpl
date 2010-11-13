	<h2>Edit User Information</h2>
	Fields in bold are required to be filled in.
  	<form action="profile.html" method="POST">		
	<table cellpadding="3" align="left">
	<tr>
		<td><b>Login: </b></td>
		<td><b>%login%</b></td>
	</tr>
	<tr>
		<td><b>E-mail address: </b></td>
		<td><b>%email%</b></td>
	</tr>
	<tr>
		<td><b>Password: </b></td>
		<td><input type="password" name="passwd" size="25" maxlength="64" value="%passwd%"></td>
	</tr>
	<tr>
		<td><b>Confirm Password: </b></td>
		<td><input type="password" name="passwd_confirm" size="25" maxlength="64" value="%passwd_confirm%">
	</tr>
	<tr>
		<td>Name: </td>
		<td><input type="text" name="name" size="25" maxlength="64" value="%name%">
	</tr>
	<tr>
		<td>Second name: </td>
		<td><input type="text" name="surname" size="25" maxlength="64" value="%surname%">
	</tr>
	<tr>
	  	<td align="center" colspan="2"><br><input type="submit" value="Save profile">&nbsp;&nbsp;<input type="reset" value="Clear"></td>
	</tr>
	</table>
  	</form>
