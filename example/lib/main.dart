import 'package:flutter/material.dart';
import 'dart:async';
import 'package:flutter_tele/flutter_tele.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatefulWidget {
  const MyApp({super.key});

  @override
  State<MyApp> createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  final TeleEndpoint _endpoint = TeleEndpoint();
  String _status = 'Initializing...';
  List<TeleCall> _calls = [];
  TeleCall? _currentCall;
  String _phoneNumber = '';
  int _selectedSim = 1;

  @override
  void initState() {
    super.initState();
    _initializeTelephony();
    _setupEventListeners();
  }

  Future<void> _initializeTelephony() async {
    try {
      setState(() {
        _status = 'Starting telephony service...';
      });

      final result = await _endpoint.start({
        'ReplaceDialer': false,
        'Permissions': false,
      });

      setState(() {
        _status = 'Telephony service started successfully';
        _calls = List<TeleCall>.from(result['calls'] ?? []);
      });

      print('Initial state: $result');
    } catch (e) {
      setState(() {
        _status = 'Error starting telephony service: $e';
      });
      print('Error initializing telephony: $e');
    }
  }

  void _setupEventListeners() {
    _endpoint.on('call_received').listen((event) {
      print('Call received: $event');
      setState(() {
        _status = 'Incoming call received';
        if (event is Map<String, dynamic>) {
          _currentCall = TeleCall.fromMap(event);
          _calls.add(_currentCall!);
        }
      });
    });

    _endpoint.on('call_changed').listen((event) {
      print('Call changed: $event');
      setState(() {
        _status = 'Call state changed';
        if (event is Map<String, dynamic>) {
          final updatedCall = TeleCall.fromMap(event);
          final index = _calls.indexWhere((call) => call.id == updatedCall.id);
          if (index != -1) {
            _calls[index] = updatedCall;
            if (_currentCall?.id == updatedCall.id) {
              _currentCall = updatedCall;
            }
          }
        }
      });
    });

    _endpoint.on('call_terminated').listen((event) {
      print('Call terminated: $event');
      setState(() {
        _status = 'Call terminated';
        if (event is Map<String, dynamic>) {
          final terminatedCall = TeleCall.fromMap(event);
          _calls.removeWhere((call) => call.id == terminatedCall.id);
          if (_currentCall?.id == terminatedCall.id) {
            _currentCall = null;
          }
        }
      });
    });

    _endpoint.on('connectivity_changed').listen((event) {
      print('Connectivity changed: $event');
      setState(() {
        _status = 'Connectivity changed: $event';
      });
    });
  }

  Future<void> _makeCall() async {
    if (_phoneNumber.isEmpty) {
      setState(() {
        _status = 'Please enter a phone number';
      });
      return;
    }

    try {
      setState(() {
        _status = 'Making call...';
      });

      final call = await _endpoint.makeCall(
        _selectedSim,
        _phoneNumber,
        null, // callSettings
        null, // msgData
      );

      setState(() {
        _currentCall = call;
        _calls.add(call);
        _status = 'Call initiated';
      });

      print('Call made: $call');
    } catch (e) {
      setState(() {
        _status = 'Error making call: $e';
      });
      print('Error making call: $e');
    }
  }

  Future<void> _answerCall() async {
    if (_currentCall == null) {
      setState(() {
        _status = 'No call to answer';
      });
      return;
    }

    try {
      await _endpoint.answerCall(_currentCall!);
      setState(() {
        _status = 'Call answered';
      });
    } catch (e) {
      setState(() {
        _status = 'Error answering call: $e';
      });
      print('Error answering call: $e');
    }
  }

  Future<void> _hangupCall() async {
    if (_currentCall == null) {
      setState(() {
        _status = 'No call to hangup';
      });
      return;
    }

    try {
      await _endpoint.hangupCall(_currentCall!);
      setState(() {
        _status = 'Call hung up';
        _currentCall = null;
      });
    } catch (e) {
      setState(() {
        _status = 'Error hanging up call: $e';
      });
      print('Error hanging up call: $e');
    }
  }

  Future<void> _declineCall() async {
    if (_currentCall == null) {
      setState(() {
        _status = 'No call to decline';
      });
      return;
    }

    try {
      await _endpoint.declineCall(_currentCall!);
      setState(() {
        _status = 'Call declined';
        _currentCall = null;
      });
    } catch (e) {
      setState(() {
        _status = 'Error declining call: $e';
      });
      print('Error declining call: $e');
    }
  }

  Future<void> _muteCall() async {
    if (_currentCall == null) {
      setState(() {
        _status = 'No call to mute';
      });
      return;
    }

    try {
      await _endpoint.muteCall(_currentCall!);
      setState(() {
        _status = 'Call muted';
      });
    } catch (e) {
      setState(() {
        _status = 'Error muting call: $e';
      });
      print('Error muting call: $e');
    }
  }

  Future<void> _unMuteCall() async {
    if (_currentCall == null) {
      setState(() {
        _status = 'No call to unmute';
      });
      return;
    }

    try {
      await _endpoint.unMuteCall(_currentCall!);
      setState(() {
        _status = 'Call unmuted';
      });
    } catch (e) {
      setState(() {
        _status = 'Error unmuting call: $e';
      });
      print('Error unmuting call: $e');
    }
  }

  Future<void> _useSpeaker() async {
    if (_currentCall == null) {
      setState(() {
        _status = 'No call to use speaker';
      });
      return;
    }

    try {
      await _endpoint.useSpeaker(_currentCall!);
      setState(() {
        _status = 'Speaker enabled';
      });
    } catch (e) {
      setState(() {
        _status = 'Error using speaker: $e';
      });
      print('Error using speaker: $e');
    }
  }

  Future<void> _useEarpiece() async {
    if (_currentCall == null) {
      setState(() {
        _status = 'No call to use earpiece';
      });
      return;
    }

    try {
      await _endpoint.useEarpiece(_currentCall!);
      setState(() {
        _status = 'Earpiece enabled';
      });
    } catch (e) {
      setState(() {
        _status = 'Error using earpiece: $e';
      });
      print('Error using earpiece: $e');
    }
  }

  @override
  void dispose() {
    _endpoint.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Flutter Tele Example',
      theme: ThemeData(
        primarySwatch: Colors.blue,
        useMaterial3: true,
      ),
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Flutter Tele Example'),
          backgroundColor: Theme.of(context).colorScheme.inversePrimary,
        ),
        body: Padding(
          padding: const EdgeInsets.all(16.0),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.stretch,
            children: [
              Card(
                child: Padding(
                  padding: const EdgeInsets.all(16.0),
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      Text(
                        'Status: $_status',
                        style: Theme.of(context).textTheme.titleMedium,
                      ),
                      const SizedBox(height: 16),
                      TextField(
                        decoration: const InputDecoration(
                          labelText: 'Phone Number',
                          border: OutlineInputBorder(),
                        ),
                        keyboardType: TextInputType.phone,
                        onChanged: (value) {
                          setState(() {
                            _phoneNumber = value;
                          });
                        },
                      ),
                      const SizedBox(height: 8),
                      Row(
                        children: [
                          const Text('SIM: '),
                          DropdownButton<int>(
                            value: _selectedSim,
                            items: const [
                              DropdownMenuItem(value: 1, child: Text('SIM 1')),
                              DropdownMenuItem(value: 2, child: Text('SIM 2')),
                            ],
                            onChanged: (value) {
                              setState(() {
                                _selectedSim = value ?? 1;
                              });
                            },
                          ),
                        ],
                      ),
                      const SizedBox(height: 16),
                      ElevatedButton(
                        onPressed: _makeCall,
                        child: const Text('Make Call'),
                      ),
                    ],
                  ),
                ),
              ),
              const SizedBox(height: 16),
              if (_currentCall != null) ...[
                Card(
                  child: Padding(
                    padding: const EdgeInsets.all(16.0),
                    child: Column(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: [
                        Text(
                          'Current Call',
                          style: Theme.of(context).textTheme.titleMedium,
                        ),
                        const SizedBox(height: 8),
                        Text('ID: ${_currentCall!.id}'),
                        Text('State: ${_currentCall!.state}'),
                        Text('Remote: ${_currentCall!.remoteNumber ?? 'Unknown'}'),
                        const SizedBox(height: 16),
                        Row(
                          mainAxisAlignment: MainAxisAlignment.spaceEvenly,
                          children: [
                            ElevatedButton(
                              onPressed: _answerCall,
                              child: const Text('Answer'),
                            ),
                            ElevatedButton(
                              onPressed: _hangupCall,
                              child: const Text('Hangup'),
                            ),
                            ElevatedButton(
                              onPressed: _declineCall,
                              child: const Text('Decline'),
                            ),
                          ],
                        ),
                        const SizedBox(height: 8),
                        Row(
                          mainAxisAlignment: MainAxisAlignment.spaceEvenly,
                          children: [
                            ElevatedButton(
                              onPressed: _muteCall,
                              child: const Text('Mute'),
                            ),
                            ElevatedButton(
                              onPressed: _unMuteCall,
                              child: const Text('Unmute'),
                            ),
                          ],
                        ),
                        const SizedBox(height: 8),
                        Row(
                          mainAxisAlignment: MainAxisAlignment.spaceEvenly,
                          children: [
                            ElevatedButton(
                              onPressed: _useSpeaker,
                              child: const Text('Speaker'),
                            ),
                            ElevatedButton(
                              onPressed: _useEarpiece,
                              child: const Text('Earpiece'),
                            ),
                          ],
                        ),
                      ],
                    ),
                  ),
                ),
              ],
              const SizedBox(height: 16),
              Expanded(
                child: Card(
                  child: Padding(
                    padding: const EdgeInsets.all(16.0),
                    child: Column(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: [
                        Text(
                          'All Calls (${_calls.length})',
                          style: Theme.of(context).textTheme.titleMedium,
                        ),
                        const SizedBox(height: 8),
                        Expanded(
                          child: ListView.builder(
                            itemCount: _calls.length,
                            itemBuilder: (context, index) {
                              final call = _calls[index];
                              return ListTile(
                                title: Text('Call ${call.id}'),
                                subtitle: Text('${call.state} - ${call.remoteNumber ?? 'Unknown'}'),
                                trailing: Text(call.getFormattedTotalDuration()),
                              );
                            },
                          ),
                        ),
                      ],
                    ),
                  ),
                ),
              ),
            ],
          ),
        ),
      ),
    );
  }
}
