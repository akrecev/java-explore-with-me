package ru.practicum.compilation.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.NewCompilationDto;
import ru.practicum.compilation.dto.UpdateCompilationRequest;
import ru.practicum.compilation.mapper.CompilationMapper;
import ru.practicum.compilation.model.Compilation;
import ru.practicum.compilation.repository.CompilationRepository;
import ru.practicum.event.model.Event;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exception.DataNotFoundException;
import ru.practicum.utility.MyPageRequest;

import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.compilation.mapper.CompilationMapper.toCompilation;
import static ru.practicum.compilation.mapper.CompilationMapper.toCompilationDto;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CompilationServiceImpl implements CompilationService {
    private final CompilationRepository compilationRepository;

    private final EventRepository eventRepository;


    @Override
    @Transactional
    public CompilationDto create(NewCompilationDto newCompilationDto) {
        List<Event> eventList = eventRepository.findAllById(newCompilationDto.getEvents());
        Compilation compilation = compilationRepository.save(toCompilation(newCompilationDto, eventList));

        return toCompilationDto(compilation);
    }

    @Override
    @Transactional
    public CompilationDto update(Long compId, UpdateCompilationRequest compilationRequest) {
        Compilation compilation = compilationRepository.findById(compId)
                .orElseThrow(() -> new DataNotFoundException("Compilation with id=" + compId + " was not found"));

        if (compilationRequest.getEvents() != null) {
            compilation.setEvents(eventRepository.findAllById(compilationRequest.getEvents()));
        }

        if (compilationRequest.getPinned() != null) {
            compilation.setPinned(compilationRequest.getPinned());
        }

        if (compilationRequest.getTitle() != null) {
            compilation.setTitle(compilationRequest.getTitle());
        }
        Compilation updateCompilation = compilationRepository.save(compilation);

        return toCompilationDto(updateCompilation);
    }

    @Override
    @Transactional
    public void deleteById(Long compId) {
        compilationRepository.findById(compId)
                .orElseThrow(() -> new DataNotFoundException("Compilation with id=" + compId + " was not found"));

        compilationRepository.deleteById(compId);
    }

    @Override
    public List<CompilationDto> getAll(Integer from, Integer size) {
        return compilationRepository.findAll(new MyPageRequest(from, size)).stream()
                .map(CompilationMapper::toCompilationDto)
                .collect(Collectors.toList());
    }

    @Override
    public CompilationDto getById(Long compId) {
        Compilation compilation = compilationRepository.findById(compId)
                .orElseThrow(() -> new DataNotFoundException("Compilation with id=" + compId + " was not found"));

        return toCompilationDto(compilation);
    }
}
