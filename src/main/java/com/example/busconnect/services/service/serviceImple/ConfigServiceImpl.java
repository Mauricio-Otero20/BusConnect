package com.example.busconnect.services.service.serviceImple;

import com.example.busconnect.api.dto.ConfigDtos.*;
import com.example.busconnect.domine.entities.Config;
import com.example.busconnect.domine.repositories.ConfigRepository;
import com.example.busconnect.services.service.ConfigService;
import com.example.busconnect.services.mappers.ConfigMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ConfigServiceImpl implements ConfigService {

    private final ConfigRepository configRepository;
    private final ConfigMapper configMapper;

    @Override
    public ConfigResponse createConfig(ConfigCreateRequest request) {
        if (configRepository.existsByKey(request.key())) {
            throw new IllegalArgumentException("Config key already exists: " + request.key());
        }

        Config config = configMapper.toEntity(request);
        Config savedConfig = configRepository.save(config);
        return configMapper.toResponse(savedConfig);
    }

    @Override
    public ConfigResponse updateConfig(Long id, ConfigUpdateRequest request) {
        Config config = configRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Config not found: " + id));

        configMapper.updateEntity(request, config);
        Config updatedConfig = configRepository.save(config);
        return configMapper.toResponse(updatedConfig);
    }

    @Override
    @Transactional
    public ConfigResponse getConfigById(Long id) {
        Config config = configRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Config not found: " + id));
        return configMapper.toResponse(config);
    }

    @Override
    @Transactional
    public ConfigResponse getConfigByKey(String key) {
        Config config = configRepository.findByKey(key)
                .orElseThrow(() -> new IllegalArgumentException("Config not found with key: " + key));
        return configMapper.toResponse(config);
    }

    @Override
    @Transactional
    public List<ConfigResponse> getAllConfigs() {
        return configRepository.findAll().stream()
                .map(configMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteConfig(Long id) {
        if (!configRepository.existsById(id)) {
            throw new IllegalArgumentException("Config not found: " + id);
        }
        configRepository.deleteById(id);
    }

    @Override
    public void deleteConfigByKey(String key) {
        if (!configRepository.existsByKey(key)) {
            throw new IllegalArgumentException("Config not found with key: " + key);
        }
        configRepository.deleteByKey(key);
    }

    @Override
    @Transactional
    public String getConfigValue(String key, String defaultValue) {
        return configRepository.findByKey(key)
                .map(Config::getValue)
                .orElse(defaultValue);
    }

    @Override
    @Transactional
    public Integer getConfigValueAsInt(String key, Integer defaultValue) {
        return configRepository.findByKey(key)
                .map(config -> {
                    try {
                        return Integer.parseInt(config.getValue());
                    } catch (NumberFormatException e) {
                        return defaultValue;
                    }
                })
                .orElse(defaultValue);
    }

    @Override
    @Transactional
    public Double getConfigValueAsDouble(String key, Double defaultValue) {
        return configRepository.findByKey(key)
                .map(config -> {
                    try {
                        return Double.parseDouble(config.getValue());
                    } catch (NumberFormatException e) {
                        return defaultValue;
                    }
                })
                .orElse(defaultValue);
    }
}
