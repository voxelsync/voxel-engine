rootProject.name = "VoxelEngine"

include(":api", ":paper", "spigot")

project(":api").projectDir = file("voxel-api/")
project(":paper").projectDir = file("voxel-paper/")
project(":spigot").projectDir = file("voxel-spigot/")