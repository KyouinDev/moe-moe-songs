# MoeMoeSongs
A small library that uses [animethemes](https://animethemes.moe) index and data.
 
### How to use
Clone this repository and then launch maven package goal.
 
### MoeCore
Almost everything is handled by the **MoeCore** class. To browse anime/game entries, you must first load them once. This is automatically done when trying to access an empty entry list.
 
Here's a sample code:
```java
public class MyClass {
    
    public static void main(String[] args) {
        System.out.println(MoeCore.getInstance().getAnimeEntries().size() + " anime loaded");
        System.out.println(MoeCore.getInstance().getGameEntries().size() + " games loaded"); //if you want game entries too
        
        //do your stuff
    }
}
```
Alternatively, you can manually update them (anytime you want) using `updateAnimeEntries(concurrentThreads: int)` and `updateGameEntries()`.
Default concurrent threads are 4, even for `getAnimeEntries()` first load, but you can modify this value according to your needs (and performances).
 
### Filters
There is a filtering system too! Specify whether or not you want anime or game entries, or both.
 
The following code line will show you how to use filters, taking for example... only Steins;Gate anime openings.
```java
List<EntrySong> filtered = EntryFilter.anime().filterEntryTitle("Steins;Gate").filterSongVersion("OP").result();
```