trunk-notes-atomspace-parser
=====================
This application reads the Trunk Notes sync folder and extracts all the knowledge contained in the *markdown* files. It processes all your notes and exports them in an existing *xmunch-atomspace* instance to be used with the proper tools.

First, it parses all notes' properties by analyzing metadata, and also the link-relations between notes **(still in progress)**.

Secondly, it also parses specific notes written in [xa-language](https://github.com/dgrmunch/xmunch-atomspace/wiki/xa-language) using the REST API of the [xmunch-atomspace (XA)](https://github.com/dgrmunch/xmunch-atomspace/wiki) to load new atoms in a running instance of it.

To force the automatic recognition of *xa-language* notes you have to tag those notes as 'xa-language'.

