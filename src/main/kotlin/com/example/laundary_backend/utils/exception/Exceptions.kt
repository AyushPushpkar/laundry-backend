package com.example.laundary_backend.utils.exception

class UserAlreadyExistsException(message: String) : RuntimeException(message)

class InvalidRequestException(message: String) : RuntimeException(message)

class InvalidRoleException(message: String) : RuntimeException(message)

class UserNotFoundException(message: String) : RuntimeException(message)

class VendorAlreadyExistsException(message: String) : RuntimeException(message)

class ResourceNotFoundException(message: String) : RuntimeException(message)

class AccessDeniedException(message: String) : RuntimeException(message)
