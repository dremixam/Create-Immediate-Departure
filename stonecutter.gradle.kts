plugins {
    id("dev.kikugie.stonecutter")
}

// The "active" node is the one your IDE indexes, and the one project-unprefixed tasks
// (./gradlew build, ./gradlew runClient) target by default. Change it with:
//   ./gradlew "1.20.1-fabric:stonecutterSwitchTo1.20.1-fabric", or use the
//   "Stonecutter switchTo..." Run Configurations the IntelliJ plugin generates automatically.
stonecutter active "1.21.1-neoforge"
