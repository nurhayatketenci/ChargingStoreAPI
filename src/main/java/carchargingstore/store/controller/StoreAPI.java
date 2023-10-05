package carchargingstore.store.controller;

import carchargingstore.store.dto.StartSessionDto;
import carchargingstore.store.dto.SummaryDto;
import carchargingstore.store.exception.StationAlreadyExistException;
import carchargingstore.store.model.Session;
import carchargingstore.store.service.StoreService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/store")
public class StoreAPI {
    private final StoreService storeService;

    public StoreAPI(StoreService storeService) {
        this.storeService = storeService;
    }
    @Operation(
            method = "POST",
            summary = "addSession",
            description = "Add new session",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Station id added successfully",
                            content = @Content(schema = @Schema(hidden = true))
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "This station id already exist"
                    )
            }
    )
    @PostMapping("/addsession/{stationId}")
    public ResponseEntity<String> addNewSession(@PathVariable String stationId) {
        try {
            storeService.addNewSession(stationId);
            return ResponseEntity.ok("Session added successfully.");
        } catch (StationAlreadyExistException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @Operation(
            method = "POST",
            summary = "chargingSessions",
            description = "Submit a new charging session for the station",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Session started successfully",
                            content = @Content( array =
                            @ArraySchema(schema = @Schema(implementation = StartSessionDto.class)))
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "station id is not null or exists"
                    )
            }
    )
    @PostMapping("/{stationId}")
    public ResponseEntity<StartSessionDto> chargingSessionStart(@PathVariable String stationId){
        StartSessionDto dto = storeService.startChargingSession(stationId);
        return new ResponseEntity<StartSessionDto>(dto,HttpStatus.OK);
    }

    @Operation(
            method = "PUT",
            summary = "chargingSessions/{id}",
            description = "Stop charging session ",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Session stoped successfully",
                            content = @Content( array =
                            @ArraySchema(schema = @Schema(implementation = Session.class)))
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "station id is not null or exists"
                    )
            }
    )
    @PutMapping("/{stationId}")
    public ResponseEntity<Session> stopChargingSession(@PathVariable String stationId){
        return new ResponseEntity<Session>(this.storeService.stopChargingSession(stationId) , HttpStatus.OK);
    }

    @Operation(
            method = "GET",
            summary = "Retrieve all charging sessions",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Retrieve all charging sessions",
                            content = @Content(
                                    array = @ArraySchema(schema = @Schema(implementation = Session.class)))),
                    @ApiResponse(
                            responseCode = "404",
                            description = "There is no available session",
                            content = @Content(schema = @Schema(hidden = true))
                    )
            }
    )
    @GetMapping
    public ResponseEntity<List<Session>>getAllSession(){
        return new ResponseEntity<List<Session>>(this.storeService.getAllSession(),HttpStatus.OK);
    }

    @Operation(
            method = "GET",
            summary = "/chargingSessions/summary",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Retrieve a summary of\n" +
                                    "submitted charging sessions\n" +
                                    "including",
                            content = @Content(
                                    array = @ArraySchema(schema = @Schema(implementation = SummaryDto.class)))),
                    @ApiResponse(
                            responseCode = "404",
                            description = "There is no session",
                            content = @Content(schema = @Schema(hidden = true))
                    )
            }
    )
    @GetMapping("/chargingSessions/summary")
    public ResponseEntity<SummaryDto> getChargingSummary(){
        return new ResponseEntity<>(this.storeService.getChargingSessionSummary(),HttpStatus.OK);
    }


}