// Author: Christopher Gill aka Kiti Gill
// This script is for a custom vehicle in GTA:SA, specifically the BF-400. All to mimic the functionality of the Oppressor Mk I.
// This version includes boost, gliding, in-air turning, and machine gun shooting with cooldown.
// Redistribution is not allowed without permission from the author.

SCRIPT_START
{
    // Declare variables, 30 variables
    // Player, NPC and car variables, 3 variables
    LVAR_INT player, car

    // FX handles. 2 variables
    LVAR_INT boostFxHandle, gunFxHandle

    // Message variables, 1 variable
    LVAR_INT hasShownMessage

    // Coords and physics, 6 variables
    LVAR_FLOAT x, y, z, currentSpeed, groundZ, heightAboveGround, liftFactor, upRightValue

    // targeting and shooting variables, 11 variables
    LVAR_FLOAT targetX, targetY, targetZ, shootX, shootY, shootZ

    // Declared INI variables, 7 variables
    LVAR_INT bikeId
    LVAR_INT boostCharge, maxBoostCharge, mgCharge, maxMgCharge
    LVAR_FLOAT boostVel, glideVel, mgAccuracyRange[2], mgAccuracy

    // Load settings from INI
    IF DOES_FILE_EXIST "cleo\\kiti_opresor.ini"
        READ_FLOAT_FROM_INI_FILE "cleo\\kiti_opresor.ini" "SETTINGS" "yourboostVel" boostVel
        READ_FLOAT_FROM_INI_FILE "cleo\\kiti_opresor.ini" "SETTINGS" "yourglideVel" glideVel
        READ_FLOAT_FROM_INI_FILE "cleo\\kiti_opresor.ini" "SETTINGS" "yourMgAccuracyRange1" mgAccuracyRange[0]
        READ_FLOAT_FROM_INI_FILE "cleo\\kiti_opresor.ini" "SETTINGS" "yourMgAccuracyRange2" mgAccuracyRange[1]
        READ_INT_FROM_INI_FILE "cleo\\kiti_opresor.ini" "SETTINGS" "yourbikeid" bikeId
        READ_INT_FROM_INI_FILE "cleo\\kiti_opresor.ini" "SETTINGS" "yourmaxBoostCharge" maxBoostCharge
        READ_INT_FROM_INI_FILE "cleo\\kiti_opresor.ini" "SETTINGS" "yourboostCharge" boostCharge
        READ_INT_FROM_INI_FILE "cleo\\kiti_opresor.ini" "SETTINGS" "yourmaxMgCharge" maxMgCharge
        READ_INT_FROM_INI_FILE "cleo\\kiti_opresor.ini" "SETTINGS" "yourmgCharge" mgCharge
    ELSE
        PRINT_STRING_NOW "ERROR: Missing INI file for Kitti Oppressor Mk1!" 5000
    ENDIF
    // End of INI loading

    // Verify wrong INI values
    IF boostVel <= 0.0
        boostVel = 50.0
    ENDIF

    IF glideVel <= 0.0
        glideVel = 28.0
    ENDIF

    IF maxBoostCharge <= 0
        maxBoostCharge = 300
    ENDIF
    IF boostCharge < 0
        boostCharge = 300
    ENDIF

    IF maxMgCharge <= 0
        maxMgCharge = 450
    ENDIF
    IF mgCharge < 0
        mgCharge = 450
    ENDIF
    // End of INI verification

    // Initialize variables
    boostFxHandle = 0
    gunFxHandle = 0
    hasShownMessage = 0
    liftFActor = 0.03
    // End of initialization

main_loop:
    WAIT 0
    GET_PLAYER_CHAR 0 player
    GET_CAR_CHAR_IS_USING player car

    IF IS_CHAR_IN_ANY_CAR player
        IF IS_CAR_MODEL car bikeId

            IF hasShownMessage = 0
                PRINT_STRING "Kitti Oppressor MkI v0.1.1 by Christopher Gill" 4000
                WAIT 4000
                hasShownMessage = 1
            ENDIF

            // Show Controls 
            WHILE IS_KEY_PRESSED VK_KEY_N
                WAIT 0
                IF IS_KEY_PRESSED VK_KEY_T
                    PRINT_STRING "Boost: Hold Left Shift  | In-Air Turning: A/D keys | Machine Gun: Left Alt" 1000
                ENDIF
            ENDWHILE
            // End of Showing Controls

            // Boost Logic
            IF IS_KEY_PRESSED VK_LSHIFT
                IF boostCharge > 150
                    SET_CAR_FORWARD_SPEED car boostVel
                    boostCharge -= 1
                    IF boostCharge < 0
                        boostCharge = 0
                    ENDIF
                    IF boostFxHandle = 0
                        CREATE_FX_SYSTEM_ON_CAR "fire" car 0.0 -1.0 0.3 0 boostFxHandle
                        REPORT_MISSION_AUDIO_EVENT_AT_CAR car 1159
                        PLAY_FX_SYSTEM boostFxHandle
                    ENDIF
                ELSE
                    IF boostFxHandle > 0
                        KILL_FX_SYSTEM boostFxHandle
                        boostFxHandle = 0
                    ENDIF
                ENDIF
            ELSE
                IF boostFxHandle > 0
                    KILL_FX_SYSTEM boostFxHandle
                    boostFxHandle = 0
                ENDIF
                IF boostCharge < maxBoostCharge
                    boostCharge += 1
                    IF boostCharge > maxBoostCharge
                        boostCharge = maxBoostCharge
                    ENDIF
                ENDIF
            // End of Boost Logic
            
                // Recharge full on ground
                IF NOT IS_CAR_IN_AIR_PROPER car
                    boostCharge = maxBoostCharge
                ENDIF
                // End of Boost Recharge
            ENDIF

            // Glide Logic    
                IF IS_CAR_IN_AIR_PROPER car
                    GET_CAR_SPEED car currentSpeed
                    GET_CAR_COORDINATES car x y z
                    GET_GROUND_Z_FOR_3D_COORD x y z groundZ
                    heightAboveGround = z - groundZ

                    GET_CAR_UPRIGHT_VALUE car upRightValue

                    IF heightAboveGround > 2.0
                        IF upRightValue > 0.8
                            
                            IF glideVel < currentSpeed
                                glideVel = currentSpeed
                            ENDIF

                            IF currentSpeed > glideVel
                                glideVel = currentSpeed
                            ENDIF
                            
                            SET_CAR_FORWARD_SPEED car glideVel


                            IF glideVel < 25.0
                                liftFactor = -0.09
                            ELSE
                                liftFactor = -0.06
                            ENDIF
                            
                            glideVel -= 0.1
                            IF glideVel < 20.0
                                glideVel = 20.0
                                APPLY_FORCE_TO_CAR car 0.01 0.0 liftFactor 0.0 0.0 0.0
                            ENDIF
                        ENDIF
                    ENDIF
                ENDIF
            // End of Glide Logic

            // In-Air Turning
            IF IS_CAR_IN_AIR_PROPER car
                IF IS_KEY_PRESSED VK_KEY_A
                    ADD_TO_CAR_ROTATION_VELOCITY car 0.0 0.0 0.05
                ENDIF
                IF IS_KEY_PRESSED VK_KEY_D
                    ADD_TO_CAR_ROTATION_VELOCITY car 0.0 0.0 -0.05
                ENDIF
                IF IS_KEY_PRESSED VK_KEY_W
                    ADD_TO_CAR_ROTATION_VELOCITY car -0.4 0.0 0.0
                ENDIF
                IF IS_KEY_PRESSED VK_KEY_S
                    ADD_TO_CAR_ROTATION_VELOCITY car 0.4 0.0 0.0
                ENDIF
            ENDIF
            // End of In-Air Turning

            // Machine Gun
            IF IS_KEY_PRESSED VK_LMENU
                IF mgCharge > 300
                    GENERATE_RANDOM_FLOAT_IN_RANGE mgAccuracyRange[0] mgAccuracyRange[1] mgAccuracy

                    GET_OFFSET_FROM_CAR_IN_WORLD_COORDS car -0.3 1.5 0.5 shootX shootY shootZ
                    GET_OFFSET_FROM_CAR_IN_WORLD_COORDS car mgAccuracy 40.0 0.8 targetX targetY targetZ
                    FIRE_SINGLE_BULLET shootX shootY shootZ targetX targetY targetZ 35

                    GET_OFFSET_FROM_CAR_IN_WORLD_COORDS car 0.3 1.5 0.5 shootX shootY shootZ
                    GET_OFFSET_FROM_CAR_IN_WORLD_COORDS car mgAccuracy 40.0 0.8 targetX targetY targetZ
                    FIRE_SINGLE_BULLET shootX shootY shootZ targetX targetY targetZ 35
                    
                    REPORT_MISSION_AUDIO_EVENT_AT_CAR car 1157
                    
                    CREATE_FX_SYSTEM_ON_CAR "gunflash" car -0.3 1.5 0.5 0 gunFxHandle
                    CREATE_FX_SYSTEM_ON_CAR "gunsmoke" car -0.3 1.5 0.5 0 gunFxHandle
                    
                    PLAY_FX_SYSTEM gunFxHandle
                    KILL_FX_SYSTEM gunFxHandle
                    
                    CREATE_FX_SYSTEM_ON_CAR "gunflash" car 0.3 1.5 0.5 0 gunFxHandle
                    CREATE_FX_SYSTEM_ON_CAR "gunsmoke" car 0.3 1.5 0.5 0 gunFxHandle
                    
                    PLAY_FX_SYSTEM gunFxHandle
                    KILL_FX_SYSTEM gunFxHandle
                    gunFxHandle = 0
                    mgCharge -= 1
                    IF mgCharge < 0
                        mgCharge = 0
                    ENDIF
                ENDIF
            ELSE
                IF mgCharge < maxMgCharge
                    mgCharge += 1
                    IF mgCharge > maxMgCharge
                        mgCharge = maxMgCharge
                    ENDIF
                ENDIF
            ENDIF
            // End of Machine Gun            

        ENDIF
    ENDIF
    // End of 'IF player in Bike'
GOTO main_loop
}
SCRIPT_END