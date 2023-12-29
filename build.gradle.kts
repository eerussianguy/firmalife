plugins {
    id("java")
    id("idea")
    id("com.matthewprenger.cursegradle") version "1.4.0"
    id("net.minecraftforge.gradle") version "[6.0,6.2)"
    id("org.parchmentmc.librarian.forgegradle") version "1.+"
    id("org.spongepowered.mixin") version "0.7.+"
}

val minecraftVersion: String = "1.20.1"
// Don't bump this unless completely necessary - this is the NeoForge + Forge compatible version
// In future we probably want to track NeoForge versions, especially post-1.20 breaking change window
val forgeVersion: String = "47.1.3"
val mixinVersion: String = "0.8.5"
val modVersion: String = System.getenv("VERSION") ?: "0.0.0-indev"
val jeiVersion: String = "15.2.0.21"
val patchouliVersion: String = "1.20.1-81-FORGE"
val jadeVersion: String = "4614153"
val topVersion: String = "4629624"
val tfcVersion: String = "4976574"

val modId: String = "firmalife"

base {
    archivesName.set("Firmalife-$minecraftVersion")
    group = "com.eerussianguy.firmalife"
    version = modVersion
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}

idea {
    module {
        excludeDirs.add(file("run"))
    }
}


repositories {
    mavenCentral()
    mavenLocal()
    maven(url = "https://dvs1.progwml6.com/files/maven/") // JEI
    maven(url = "https://modmaven.k-4u.nl") // Mirror for JEI
    maven(url = "https://maven.blamejared.com") // Patchouli
    maven(url = "https://www.cursemaven.com") {
        content {
            includeGroup("curse.maven")
        }
    }
    flatDir {
        dirs("libs")
    }
}

dependencies {
    minecraft("net.minecraftforge", "forge", version = "$minecraftVersion-$forgeVersion")
    // TFC
    implementation(fg.deobf("curse.maven:tfc-302973:${tfcVersion}"))
//    implementation(fg.deobf("tfc:TerraFirmaCraft-Forge-1.20.1:0.0.0-indev"))

    // JEI
    compileOnly(fg.deobf("mezz.jei:jei-$minecraftVersion-forge-api:$jeiVersion"))
    compileOnly(fg.deobf("mezz.jei:jei-$minecraftVersion-common-api:$jeiVersion"))
    runtimeOnly(fg.deobf("mezz.jei:jei-$minecraftVersion-forge:$jeiVersion"))

    // Patchouli
    // We need to compile against the full JAR, not just the API, because we do some egregious hacks.
    compileOnly(fg.deobf("vazkii.patchouli:Patchouli:$patchouliVersion"))
    runtimeOnly(fg.deobf("vazkii.patchouli:Patchouli:$patchouliVersion"))

    // Jade / The One Probe
    compileOnly(fg.deobf("curse.maven:jade-324717:${jadeVersion}"))
    compileOnly(fg.deobf("curse.maven:top-245211:${topVersion}"))

    // Only use Jade at runtime
    runtimeOnly(fg.deobf("curse.maven:jade-324717:${jadeVersion}"))

    if (System.getProperty("idea.sync.active") != "true") {
        annotationProcessor("org.spongepowered:mixin:${mixinVersion}:processor")
    }

}

minecraft {
    mappings("parchment", "2023.09.03-1.20.1")
    accessTransformer(file("src/main/resources/META-INF/accesstransformer.cfg"))

    runs {
        all {
            args("-mixin.config=$modId.mixins.json")

            property("forge.logging.console.level", "debug")

            property("mixin.env.remapRefMap", "true")
            property("mixin.env.refMapRemappingFile", "$projectDir/build/createSrgToMcp/output.srg")

            jvmArgs("-ea", "-Xmx4G", "-Xms4G")

            jvmArg("-XX:+AllowEnhancedClassRedefinition")

            mods.create(modId) {
                source(sourceSets.main.get())
            }
        }

        register("client") {
            workingDirectory(project.file("run/client"))
        }

        register("server") {
            workingDirectory(project.file("run/server"))

            arg("--nogui")
        }

    }
}

mixin {
    add(sourceSets.main.get(), "firmalife.refmap.json")
}

tasks {
    jar {
        manifest {
            attributes["Implementation-Version"] = project.version
            attributes["MixinConfigs"] = "$modId.mixins.json"
        }
    }
}
