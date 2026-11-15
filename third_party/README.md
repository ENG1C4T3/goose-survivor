# Third-party licences and notices

This directory records licence texts and selected accompanying notices for
libGDX 1.14.2 and Ashley 1.7.4, as declared in
`goose-survivor/build.gradle.kts`.

These files describe third-party software. They do not assign a licence to
Goose Survivor's own code or assets.

## Libraries and source versions

| Library | Dependencies covered by this documentation | Main licence |
| --- | --- | --- |
| libGDX 1.14.2 | `gdx`, `gdx-backend-lwjgl3`, `gdx-platform:natives-desktop` | Apache-2.0, with separately licensed embedded code noted below |
| Ashley 1.7.4 | `com.badlogicgames.ashley:ashley` | Apache-2.0 |

Official source tags inspected:

- libGDX: `1.14.2`, commit `6b164672625ff2f009d3628e592e032b9269f112`
- Ashley: `1.7.4`, commit `0dfdf5257097e57ea9bead170e8eeb62647d57d8`

## Included files and provenance

- [libGDX licence](libgdx/LICENSE):
  copied from the official [1.14.2 LICENSE](https://github.com/libgdx/libgdx/blob/1.14.2/LICENSE).
- [Ashley licence](ashley/LICENSE):
  copied from the official [1.7.4 LICENSE](https://github.com/libgdx/ashley/blob/1.7.4/LICENSE).
- [Ashley NOTICE](ashley/NOTICE):
  copied from the official [1.7.4 NOTICE](https://github.com/libgdx/ashley/blob/1.7.4/NOTICE),
  retaining its copyright and Ash/Artemis attribution.
- [Slick 2D licence](libgdx/SLICK2D-LICENSE.txt):
  the complete opening licence comment from libGDX 1.14.2's
  [OggInputStream.java](https://github.com/libgdx/libgdx/blob/1.14.2/backends/gdx-backend-lwjgl3/src/com/badlogic/gdx/backends/lwjgl3/audio/OggInputStream.java).
  This code carries BSD-style redistribution conditions.
- [stb_image licence](libgdx/STB-IMAGE-LICENSE.txt):
  the licence section from libGDX 1.14.2's
  [stb_image.h](https://github.com/libgdx/libgdx/blob/1.14.2/gdx/jni/gdx2d/stb_image.h).
  It offers MIT and public-domain/Unlicense alternatives.
  Both alternatives are retained here; this project documents use under
  the MIT alternative.

## Applicability and redistribution

Apache-2.0 permits use in this university project, subject to its applicable
conditions. Section 4 addresses providing the licence, marking modified
upstream files, retaining relevant source notices, and reproducing applicable
NOTICE attributions. Merely listing library names in a report does not
demonstrate that these conditions have been met.

This directory supplies repository-level licence and attribution material.
It does not establish that a future game distribution includes these files.
Before distributing a release, include the applicable texts with that release
and check its actual contents and any modifications to upstream code.

The Slick 2D and stb_image texts must also be considered under their own terms;
the Apache licence alone does not replace them.

## Scope and limitations

This is a focused record for the two libraries above and the identified
embedded-code notices, not a complete audit of the dependency graph or assets.
Separate dependencies such as LWJGL, audio libraries, build/test tools and
game assets are outside this record.

The libGDX source tree also contains `THIRDPARTY` (ANGLE/MetalANGLE notices)
and `CC-LICENSE`. Their presence alone does not establish applicability to
this project's selected dependencies or assets. Reassess them if relevant
components or upstream assets are included.

No packaging or runtime verification was performed as part of this
documentation task.
