# wombat-text-editor

<p align="center">
  <img src="https://preview.ibb.co/gbFZvf/b00m.png" alt="Wombatik Demo" />
</p>

### Dead simple text editor, with multi-threading support. 

This is just a demo program for my 'Sistem Operasi' assignment (thanks, professor). 
Anyway, please ask any questions (either related or not related one, just don't ask something nonsense) on issues section.

### What's with (inaccurate) label below Sentence count?

It's impossible to determine a sentence without doing comprehensive analysis (Natural Language Processing). There's to much factors to consider (aposthrope, commas, and many more).

### Huh? I don't see any mutual exclusion part here...

Of course, JavaFX is using `Platform.runLater( // thread here )` to handle critical regions. It will ensure that any changes on UI will happen in the corresponding thread. JavaFX will reject other mutual exclusion algorithm.

### Last Question, What's with `wombat`?

To honour my _alma mater_, `wombat` is a kind of convention here.

# TODOS

- [x] Update the name. (Uuh.. I don't think a good name is necessary for this project)

# Changelog

- v1.1 - Added keyboard shortcut and line 'jump' features
- v1.0 - Initial Release
