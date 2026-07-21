// Author: Christopher Gill aka Kiti Gill
// This script is for a custom vehicle in GTA:SA, specifically the BF-400. All to mimic the functionality of the Oppressor Mk I.
// This version includes boost, gliding, in-air turning, and machine gun shooting with cooldown.
// Redistribution is not allowed without permission from the author.

SCRIPT_START
{
    // Declare variables, ? variables
    // Player, NPC, car and targets variables, 4 variables
    LVAR_INT player, car, targetNpc, obstacleNpc, obstacleCar

    // FX handles. ? variables
    

    // Vehicle and npc that comes in the way of target, 4 variables
    LVAR_FLOAT targetInWayX1, targetInWayY1, targetInWayZ1, targetInWayX2, targetInWayY2

    // targeting and shooting variables, 12 variables
    LVAR_INT blip, missil, target, missileActive
    LVAR_FLOAT targetX, targetY, targetZ, shootX, shootY, shootZ, angle, dist

    // Declared INI variables, 1 variables
    LVAR_INT bikeId

    // Load settings from INI
    IF DOES_FILE_EXIST "cleo\\kiti_opresor.ini"
        READ_INT_FROM_INI_FILE "cleo\\kiti_opresor.ini" "SETTINGS" "yourbikeid" bikeId
    ENDIF
    // End of INI loading

main_loop:
    WAIT 0
    GET_PLAYER_CHAR 0 player

    IF IS_CHAR_IN_ANY_CAR player
        GET_CAR_CHAR_IS_USING player car
        IF IS_CAR_MODEL car bikeId

            // incease Bike durability
            SET_CAR_HEALTH car 2000

            // Rocket without lock-on
            IF NOT IS_KEY_PRESSED VK_RBUTTON
                IF IS_KEY_PRESSED VK_LBUTTON
                    REQUEST_MODEL 345
                    LOAD_ALL_MODELS_NOW

                    // Get start and target coordinates
                    GET_OFFSET_FROM_CAR_IN_WORLD_COORDS car 0.0 1.5 0.5 shootX shootY shootZ
                    GET_OFFSET_FROM_CAR_IN_WORLD_COORDS car 0.0 18.0 0.8 targetX targetY targetZ

                    // Create rocket at launch position
                    CREATE_OBJECT 345 shootX shootY shootZ missil
                    GET_CAR_HEADING car angle
                    SET_OBJECT_HEADING missil angle

                    // Calculate direction vector (target - shoot)
                    targetX -= shootX
                    targetY -= shootY
                    targetZ -= shootZ

                    // Scale to movement speed per tick
                    targetX *= 0.12
                    targetY *= 0.12
                    targetZ *= 0.12

                    REPEAT 50 target
                        WAIT 10
                        shootX += targetX
                        shootY += targetY
                        shootZ += targetZ
                        SET_OBJECT_COORDINATES missil shootX shootY shootZ

                        // Vehicle in path detection
                        targetInWayX1 = shootX - 5.0
                        targetInWayY1 = shootY - 5.0
                        targetInWayX2 = shootX + 5.0
                        targetInWayY2 = shootY + 5.0
                        GET_RANDOM_CAR_OF_TYPE_IN_AREA targetInWayX1 targetInWayY1 targetInWayX2 targetInWayY2 -1 obstacleCar

                        // Ped in path detection (includes police, medics, etc.)
                        GET_RANDOM_CHAR_IN_SPHERE_NO_SAVE_RECURSIVE shootX shootY shootZ 1.5 0 1 obstacleNpc

                        IF DOES_VEHICLE_EXIST obstacleCar
                            IF NOT obstacleCar = car
                                GOTO explode
                            ENDIF
                        ENDIF

                        IF DOES_CHAR_EXIST obstacleNpc
                            GOTO explode
                        ENDIF

                        IF IS_OBJECT_INTERSECTING_WORLD missil
                            GOTO explode
                        ENDIF
                    ENDREPEAT

                    GOTO explode
                ENDIF
            ENDIF
            // End of Rocket without lock-on

            // Locking Rocket Target
            IF IS_KEY_PRESSED VK_RBUTTON

                // Get Target
                GET_OFFSET_FROM_CAR_IN_WORLD_COORDS car 0.0 60.0 0.0 targetX targetY targetZ
                GET_RANDOM_CHAR_IN_SPHERE_NO_SAVE_RECURSIVE targetX targetY targetZ 30.0 1 1 targetNpc


                // Lock Target
                IF DOES_CHAR_EXIST targetNpc
                    ADD_BLIP_FOR_CHAR targetNpc blip
                    PRINT_STRING_NOW "Target found" 0
                    WHILE IS_KEY_PRESSED VK_RBUTTON
                        WAIT 0

                        IF IS_KEY_PRESSED VK_LBUTTON
                            GOTO shoot
                        ENDIF
                    ENDWHILE
                    REMOVE_BLIP blip
                    MARK_CHAR_AS_NO_LONGER_NEEDED targetNpc
                    targetNpc = 0
                ELSE
                    PRINT_STRING_NOW "No target was found" 0
                ENDIF            
            ELSE
                REMOVE_BLIP blip
                MARK_CHAR_AS_NO_LONGER_NEEDED targetNpc
                targetNpc = 0
            ENDIF
            // End of Locking Target

        ENDIF
    ENDIF
    // End of 'IF player in Bike'
GOTO main_loop

shoot:
    
    PRINT_STRING "Rocket Missile Released" 1000
    REQUEST_MODEL 345
    LOAD_ALL_MODELS_NOW
    GET_CAR_HEADING car angle
    GET_OFFSET_FROM_CAR_IN_WORLD_COORDS car 0.0 1.5 0.5 shootX shootY shootZ
    CREATE_OBJECT 345 shootX shootY shootZ missil
    SET_OBJECT_HEADING missil angle
GOTO seek

seek:
    REPEAT 100 target
        WAIT 5

        IF DOES_CHAR_EXIST targetNpc
            GET_CHAR_COORDINATES targetNpc targetX targetY targetZ
        ENDIF

        IF shootX > targetX
            shootX -= 1.0
        ENDIF
        IF shootX < targetX
            shootX += 1.0
        ENDIF

        IF shootY > targetY
            shootY -= 1.0
        ENDIF
        IF shootY < targetY
            shootY += 1.0
        ENDIF

        IF shootZ > targetZ
            shootZ -= 1.0
        ENDIF
        IF shootZ < targetZ
            shootZ += 1.0
        ENDIF

        // Get vehicles in the way
        targetInWayX1 = shootX - 5.0
        targetInWayY1 = shootY - 5.0
        targetInWayX2 = shootX + 5.0
        targetInWayY2 = shootY + 5.0
        GET_RANDOM_CAR_OF_TYPE_IN_AREA targetInWayX1 targetInWayY1 targetInWayX2 targetInWayY2 -1 obstacleCar

        // Also check for any ped in the way using CLEO+ command
        GET_RANDOM_CHAR_IN_SPHERE_NO_SAVE_RECURSIVE shootX shootY shootZ 1.5 0 7 obstacleNpc

        // Rotate missile toward target if still valid
        IF DOES_CHAR_EXIST targetNpc
            GET_CHAR_HEADING targetNpc angle
            SET_OBJECT_HEADING missil angle
        ENDIF

        // If vehicle in the way (but not our own bike)
        IF DOES_VEHICLE_EXIST obstacleCar
            IF NOT obstacleCar = car
                GOTO explode
            ENDIF
        ENDIF

        // If any NPC (medic, police, etc.) in the path
        IF DOES_CHAR_EXIST obstacleNpc
            GOTO explode
        ENDIF

        SET_OBJECT_COORDINATES missil shootX shootY shootZ
        GET_DISTANCE_BETWEEN_COORDS_3D shootX shootY shootZ targetX targetY targetZ dist
        IF dist < 1.1
            GOTO explode
        ENDIF
    ENDREPEAT

explode:
    DELETE_OBJECT missil
    MARK_MODEL_AS_NO_LONGER_NEEDED 345
    ADD_EXPLOSION shootX shootY shootZ EXPLOSION_ROCKET
    REMOVE_BLIP blip
    MARK_CHAR_AS_NO_LONGER_NEEDED targetNpc
    targetNpc = 0
GOTO main_loop
}
SCRIPT_END