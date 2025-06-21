rootProject.name = "VoxelEngine"

include(":api", ":paper")

project(":api").projectDir = file("voxel-api/")
project(":paper").projectDir = file("voxel-paper/")