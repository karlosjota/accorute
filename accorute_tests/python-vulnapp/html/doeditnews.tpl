<h2>Edit News</h2>
<form action="doeditnews.html" method="POST">
<table width = "100%" cellspacing="3" align="left">
<tr>
	<td>Title:</td>
	<td><input type="text" name="title" size="80" value="%title%"></td>
</tr>
<tr>
	<td>Text:</td> 
	<td><textarea name="body" cols="75" rows="10">%body%</textarea></td>
</tr>
<tr>
	<td colspan="2" align="center"><br>
	<input type="submit" value="Save changes!">
	<input type="hidden" name="id" value="%id%">
	</td>
</tr>
<tr>
	<td colspan="2" align="right"><br>
	<a href="editnews.html">Back to edit news page</a>
	</td>
</tr>
</table>
</form>
