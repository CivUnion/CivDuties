plugins {
	`java-library`
	id("io.papermc.paperweight.userdev") version "1.3.8"
}

dependencies {
	paperDevBundle("1.18.2-R0.1-SNAPSHOT")

    compileOnly("com.github.MilkBowl:VaultAPI:1.7")
	compileOnly("net.civmc.civmodcore:CivModCore:2.4.0:dev-all")
    compileOnly("net.civmc.combattagplus:CombatTagPlus:2.1.0:dev")
}