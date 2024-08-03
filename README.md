# GackoHitboxes
Library for advanced multihitbox mobs. Can sync hitboxes to geckolib bones

The goal is to simplify the process of creating multipart entities. Also it allows synching hitboxes to geckolib animations!

# Downloads
- [Curseforge](https://www.curseforge.com/minecraft/mc-mods/multihitboxlib)
- [Modrinth](https://modrinth.com/mod/multihitboxlib)

# Terms of use
The license needs to be met (GNU license).

In addition to that, the following points must be met too:
- Jarinjaring, shadowing or directly including the source code or a built jar of this project in any way is not allowed unless otherwisely discussed with me personally.
- Forks and ports: not allowed. period. If you want it for a different version, create a pull request on github.
- Forks in any form of the library created for the intent to publish it somewhere else are not allowed. Excuses are to be aranged with me via e-mail contact (dertoaster@cq-repoured.net). Pull requests are allowed and welcome. Reuploading forks or ports to other mod loaders or minecraft versions are not permitted, if you want it for different versions, create a pull request on github.
- Complete re-uploads (of the source code or builds) on other sites is not permitted unless explicitly permitted by me.
- Usage of the library in commercial projects  (as in you get any form of direct monetary revenue for this (e.g. writing a mod for some youtuber)) is not permitted by default. For permission, contact me via e-mail (dertoaster@cq-repoured.net) directly
- If the library is being used, the authors of this library have to be mentioned at least in the mod's credits section
- If the library is being used in a commercial (as in you get any form of direct monetary revenue for this (e.g. writing a mod for some youtuber)) projects, direct credit has to be given and we need to be contacted first for permission

In short: as a user installing this mod, just install it normally. If you are a developer that wants to use this library in their mod: don't be a dick, depend on it NORMALLY without including this jar or the source code in your stuff and if you make profits via some "40k$ mr beast mod" stuff then please ask in before if it is alright. Leeching from the work of others isn't cool.

# Using (for mod developers)
To use MHLib, either use cursemaven or ivy.
For ivy, you need to do this:
```
repositories {
    ivy {
            name "Github Releases - DT Versioning" // Github Releases
            url "https://github.com"

            patternLayout {
                artifact "[organisation]/[module]/releases/download/MC[revision]/[module]-[revision].[ext]"
            }

            metadataSources { artifact() }
        }
}

dependencies {
    implementation fg.deobf("dertoaster98:multihitboxlib:${mc_version}-${multihitboxlib_version}@jar")
}
```
${mc_version} and ${multihitboxlib_version} are parameters drawn from gradle.properties, so set them there.
Example:
```
mc_version=1.20.1
multihitboxlib_version=1.6.0
```

Please depend on the library like a normal mod!
That in essence means add it to your mods requirements list and don't shadow or jar-in-jar or directly include it or similar in your mod's jar or source code.

# Contact
- Discord: dertoaster
- E-Mail (preferred): [dertoaster@cq-repoured.net](mailto:dertoaster@cq-repoured.net?subject=[MHLib]%20contact%20request)
