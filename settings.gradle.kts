rootProject.name = "VoxelEngine"

include(":api", ":paper", ":velocity", ":common")

project(":api").projectDir = file("voxel-api/")
project(":common").projectDir = file("voxel-common/")

project(":paper").projectDir = file("voxel-paper/")
project(":velocity").projectDir = file("voxel-velocity/")