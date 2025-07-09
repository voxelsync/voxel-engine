rootProject.name = "VoxelEngine"

include(":api", ":paper", ":velocity")

project(":api").projectDir = file("voxel-api/")

project(":paper").projectDir = file("voxel-paper/")
project(":velocity").projectDir = file("voxel-velocity/")