easy jsp forum
http://sourceforge.net/projects/easyjspforum/

This web app is taken from the UCSB paper.

To obtain results, AcCoRuTe tool was run 3 times.

During the first run, the following flaws were detected:
0. [false positive] Any user can view profile of any other user.
This is not a flaw itself, but, since the "view profile" links only exist
for members that posted a message, it was reported.
1. "Edit forum" page was available for moderator not assigned to this forum
2. "Edit forum" action was available for moderator not assigned to this forum
3. "Delete forum" was available for moderator not assigned to this forum
The last two flaws are state-changing, thus the tool stopped evaluation preliminary.

During the second run, flaws 2 and 3 were added to "not test" suppression list.
The following flaw was additionally recognized:
4. "Delete thread" action is available for any registered user who knows the link

During the third run, flaws 2,3,4 were suppressed. No additional flaws were discovered.

5. [false negative] The action "Modify thread" is also vulnerable, but due to a bug in
HtmlUnit javascript engine, the action itself wasn't discovered.

flaws 5 and 4 are described in Felmetsger et al.
Flaws 1,2,3 were previously unknown.
